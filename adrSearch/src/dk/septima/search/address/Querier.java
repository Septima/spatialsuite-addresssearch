package dk.septima.search.address;

import java.sql.Connection;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.carlbro.cbinfo.datasource.datasourcemanager.DatasourceManager;
import com.carlbro.cbinfo.datasource.impl.database.DBConnection;
import com.carlbro.cbinfo.datasource.impl.database.DBContext;
import com.carlbro.cbinfo.datasource.impl.database.DBEndpoint;
import com.carlbro.cbinfo.datasource.impl.database.command.DBSqlCommand;
import com.carlbro.cbinfo.global.GlobalRessources;
import com.carlbro.cbinfo.util.XMLTools2;
import com.carlbro.jdaf.pcollection.Row;
import com.carlbro.jdaf.pcollection.RowList;
import com.carlbro.jdaf.pcollection.RowMetadata;
import com.carlbro.jdaf.xml.DocumentCache;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

public class Querier {

	public static String query(String input, int maxResults) throws Exception{
		try{
			GlobalRessources.getInstance().reloadIfNeeded();
			DocumentCache docCache = GlobalRessources.getInstance ().getDocumentCache ();
			String configDir = GlobalRessources.getInstance().getCBInfoParam().getLocalStringValue("adrSearch.config.dir");
			Document configDoc = docCache.getDocument(configDir + "/config.xml");
			Node nEndpoint = XMLTools2.getNodeList(configDoc, "//endpoint").item(0);
			DBConnection dbConnection = getDbConnection(nEndpoint);
			Connection connection = dbConnection.getPConnection().getConnection();
			List<SearchResult> searchResult = Searcher.search(input, connection, maxResults);
			
			int searchResultCount = searchResult.size();
			JsonObject returnObject = new JsonObject();
			returnObject.add("numHits", searchResultCount);
			returnObject.add("status", "OK");
			returnObject.add("message", "OK");
			JsonArray data = new JsonArray();
			for (int i=0;i<searchResultCount;i++){
				data.add(createObject(searchResult.get(i)));
			}
			returnObject.add("data", data);
			return returnObject.toString();
		}catch(Exception e){
			JsonObject returnObject = new JsonObject();
			returnObject.add("status", "ERROR");
			returnObject.add("message", e.getMessage());
			return returnObject.toString();
		}
	}
	
	private static JsonObject createObject(SearchResult searchResult) {
		String resultType = searchResult.getType();
		JsonObject returnObject = new JsonObject();
		if (resultType.equalsIgnoreCase("addressAccessType")){
			AddressAccessType result = (AddressAccessType)searchResult;
			returnObject.add("type", resultType);
			returnObject.add("streetName", result.streetName);
			returnObject.add("streetBuildingIdentifier", result.streetBuildingIdentifier);
			returnObject.add("postCodeIdentifier", result.postCodeIdentifier);
			returnObject.add("districtName", result.districtName);
			returnObject.add("presentationString", result.presentationString);
			returnObject.add("geometryWkt", result.geometryWkt);
		}else if (resultType.equalsIgnoreCase("streetNameType")){
			StreetNameType result = (StreetNameType)searchResult;
			returnObject.add("type", resultType);
			returnObject.add("streetName", result.streetName);
			returnObject.add("postCodeIdentifier", result.postCodeIdentifier);
			returnObject.add("districtName", result.districtName);
			returnObject.add("presentationString", result.presentationString);
		}
		return returnObject;
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

	private static Row getDummyInput() throws Exception {
		return GlobalRessources.getInstance().getCBInfoParam().toRowList().row(0);
	}

}
