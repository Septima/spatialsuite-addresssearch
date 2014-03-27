package dk.septima.search.address;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.carlbro.cbinfo.datasource.datasourcemanager.Datasource;
import com.carlbro.cbinfo.datasource.datasourcemanager.DatasourceManager;
import com.carlbro.cbinfo.datasource.impl.database.DBConnection;
import com.carlbro.cbinfo.datasource.impl.database.DBContext;
import com.carlbro.cbinfo.datasource.impl.database.DBEndpoint;
import com.carlbro.cbinfo.datasource.impl.database.command.DBSqlCommand;
import com.carlbro.cbinfo.datasource.impl.embedded.EmbeddedEndpoint;
import com.carlbro.cbinfo.global.GlobalRessources;
import com.carlbro.cbinfo.util.XMLTools2;
import com.carlbro.jdaf.pcollection.Row;
import com.carlbro.jdaf.pcollection.RowList;
import com.carlbro.jdaf.pcollection.RowMetadata;
import com.carlbro.jdaf.xml.DocumentCache;

public class Indexer {
	
	public void build() throws Exception{
		GlobalRessources.getInstance().reloadIfNeeded();
		DocumentCache docCache = GlobalRessources.getInstance ().getDocumentCache ();
		String configDir = GlobalRessources.getInstance().getCBInfoParam().getLocalStringValue("adrSearch.config.dir");
		Document configDoc = docCache.getDocument(configDir + "/config.xml");
		Node nEndpoint = XMLTools2.getNodeList(configDoc, "//endpoint").item(0);
		Node nDatasource = XMLTools2.getNodeList(configDoc, "//datasource").item(0);
		RowList readRowList = getRowList(nDatasource);
		if (readRowList != null){
			DBConnection dbConnection = getDbConnection(nEndpoint);
			try{
				dbConnection.beginTransaction();
				executeStatement(dbConnection, "delete from adrsearch.addressaccess", getDummyInput());
				PreparedStatement insertAddressStatement = getInsertAddressStatement(dbConnection);
				int sortOrder = 0;
				for (int i=0;i<readRowList.size();i++){
					Row thisRow = readRowList.row(i);
					String addressaccessid = thisRow.column("addressaccessid").toString();
					String streetname = thisRow.column("streetname").toString();
					String streetbuildingidentifier = thisRow.column("streetbuildingidentifier").toString();
					String postcodeidentifier = thisRow.column("postcodeidentifier").toString();
					String districtname = thisRow.column("districtname").toString();
					String presentationstring = thisRow.column("presentationstring").toString();
					String geometrywkt = thisRow.column("geometrywkt").toString();
					insertAddressStatement.setString(1, addressaccessid);
					insertAddressStatement.setString(2, streetname);
					insertAddressStatement.setString(3, streetbuildingidentifier);
					insertAddressStatement.setString(4, postcodeidentifier);
					insertAddressStatement.setString(5, districtname);
					insertAddressStatement.setString(6, presentationstring);
					insertAddressStatement.setString(7, geometrywkt);
					insertAddressStatement.setInt (8 , sortOrder);
					sortOrder++;
					insertAddressStatement.addBatch();
				}
				insertAddressStatement.executeBatch();
				executeStatement(dbConnection, "delete from adrsearch.streetname", getDummyInput());
				String updateStreetnameSql;
				updateStreetnameSql  = "insert into adrsearch.streetname ";
				updateStreetnameSql += "select	max(addressaccessid) as id, ";
				updateStreetnameSql += "		streetname as streetname, ";
				updateStreetnameSql += "		postcodeidentifier as postcodeidentifier, ";
				updateStreetnameSql += "		districtname as districtname, ";
				updateStreetnameSql += "		streetname || ' (' || postcodeidentifier || ' ' ||districtname || ')' as presentationstring ";
				updateStreetnameSql += "	from	adrsearch.addressaccess ";
				updateStreetnameSql += "	group	by streetname, postcodeidentifier, districtname, streetname || ' (' || postcodeidentifier || ' ' ||districtname || ')' ";
				executeStatement(dbConnection, updateStreetnameSql, getDummyInput());
				dbConnection.commitTransaction();
			}catch(Exception e){
				dbConnection.rollbackTransaction();
				if (e instanceof java.sql.BatchUpdateException && e.getMessage().toLowerCase().indexOf("getnext") > -1){
					throw new Exception (e.getMessage() + "; " + ((java.sql.BatchUpdateException)e).getNextException().getMessage(), e);
				}else{
					throw (e);
				}
			}finally{
				if (dbConnection != null){
					dbConnection.close();
				}
			}
		}
	}

	private static Row getDummyInput() throws Exception {
		return GlobalRessources.getInstance().getCBInfoParam().toRowList().row(0);
	}

	private static DBConnection getDbConnection(Node nEndpoint) throws Exception{
		String epName = XMLTools2.getNodeValue(nEndpoint);
		DatasourceManager datasourceManager = GlobalRessources.getInstance().getDatasourceManager();
		DBEndpoint dbEndpoint = (DBEndpoint)datasourceManager.getEndpoint(epName);
		DBConnection dbConnection = (DBConnection) dbEndpoint.createConnection();
        try{
            executeStatement(dbConnection, "select * from adrsearch.addressaccess where 1=1", getDummyInput());
		}catch(Exception e){
			throw new Exception("dk.septima.search.address.Indexer: dbEndPoint '" + epName + "' not defined or database not ready for index.", e);
		}
		return dbConnection;
	}

	private static PreparedStatement getInsertAddressStatement(DBConnection dbConnection) throws Exception {
		Connection connection = dbConnection.getPConnection().getConnection();
		String insertAddressSql =  "insert into adrsearch.addressaccess (addressaccessid, streetname, streetbuildingidentifier, postcodeidentifier, districtname, presentationstring, geometrywkt, sortorder) values(?,?,?,?,?,?,?,?)";
		PreparedStatement insertAddressStatement = connection.prepareStatement(insertAddressSql);
		return insertAddressStatement;
	}
	
	private static RowList getRowList(Node nDatasource) throws Exception {
		String datasourceName = XMLTools2.getAttribute(nDatasource, "name");
		String commandName = XMLTools2.getAttribute(nDatasource, "command");
		DatasourceManager datasourceManager = GlobalRessources.getInstance().getDatasourceManager();
		Datasource datasource = datasourceManager.getDatasource(datasourceName);
		RowList readRowList = (RowList) datasource.createConnection().execute(datasource.getCommand(commandName), datasource.getContext(), getDummyInput());
		return readRowList;
	}
	
    private static RowList executeStatement(DBConnection dbConnection, String statement, Row input) throws Exception {
        RowList emptyRowList = new RowList("empty", new RowMetadata(0));
        if (statement.trim().length()>3){
            DBSqlCommand command = new DBSqlCommand("create", statement);
            RowList returnRowList = (RowList) dbConnection.execute(command, new DBContext(), input);
            if (returnRowList == null && statement.toLowerCase().indexOf("select") > -1){
                return emptyRowList;
            }else{
                return returnRowList;
            }
        } else {
            return emptyRowList;
        }
    }

}
