package dk.septima.search.address;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.carlbro.cbinfo.datasource.impl.postgis.PGEndpoint;

public class Searcher {
	
	public static List<SearchResult> search(String input, Connection connection, int maxResults, String endPointType) throws Exception{
		
		ParseResult parseResult = Parser.parse(input.toLowerCase());
		String pgLimitClause = "";
		String msSqlTopClause = "";
		if (endPointType == PGEndpoint.DBTYPE){
			pgLimitClause = " limit " + Integer.toString(maxResults) + " ";
		}else{
			msSqlTopClause = " top " + Integer.toString(maxResults) + " ";
		}

		String select = "";
		String streetSelect = "select " + msSqlTopClause + " * from streetname";
		String adrSelect = "select " + msSqlTopClause + " * from addressaccess";
		String clause = "  ";
		String streetOrderClause = " order by streetName, postcodeidentifier";
		String addressOrderClause = " order by streetName, postcodeidentifier, sortorder";
		
		if (parseResult.hasStreetName()){
			clause += " where LOWER(streetname) like '" + parseResult.streetName + "%' "; 
			if (parseResult.hasPostCode()){
				clause += " and postcodeidentifier = '" + parseResult.postCode + "' "; 
			}
			if (parseResult.hasStreetbuildingidentifier()){
				clause += " and streetbuildingidentifier like '" + parseResult.streetbuildingidentifier + "%' "; 
				select = adrSelect + clause + addressOrderClause + pgLimitClause;
				return createAddressSearchResults(connection, select);
			}else{
				select = streetSelect + clause + streetOrderClause + pgLimitClause;
				List<SearchResult> searchResults = createStreetSearchResults(connection, select);
				if (searchResults.size() > 1){
					return searchResults;
				}else{
					select = adrSelect + clause + addressOrderClause + pgLimitClause;
					return createAddressSearchResults(connection, select);
				}
			}

		}else{
			if (parseResult.hasPostCode()){
				clause += " where postcodeidentifier = '" + parseResult.postCode + "' "; 
				if (parseResult.hasStreetbuildingidentifier()){
					clause += " and streetbuildingidentifier like '" + parseResult.streetbuildingidentifier + "%' "; 
					select = adrSelect + clause + addressOrderClause + pgLimitClause;
					return createAddressSearchResults(connection, select);
				}else{
					select = streetSelect + clause + streetOrderClause + pgLimitClause;
					List<SearchResult> searchResults = createStreetSearchResults(connection, select);
					if (searchResults.size() > 1){
						return searchResults;
					}else{
						select = adrSelect += clause + addressOrderClause + pgLimitClause;
						return createAddressSearchResults(connection, select);
					}
				}
			}else{
				if (parseResult.hasStreetbuildingidentifier()){
					clause += " and streetbuildingidentifier like '" + parseResult.streetbuildingidentifier + "%' "; 
					select = adrSelect + clause + addressOrderClause + pgLimitClause;
					return createAddressSearchResults(connection, select);
				}else{
					return new ArrayList<SearchResult>();
				}
			}
		}
	}

	private static List<SearchResult> createAddressSearchResults(Connection connection, String select) throws Exception {
		List<SearchResult> list = new ArrayList<SearchResult>();
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(select);
		while (rs.next()){
			AddressAccessType AddressAccessType = new AddressAccessType();
			AddressAccessType.streetName = rs.getString("streetname");
			AddressAccessType.streetBuildingIdentifier = rs.getString("streetbuildingidentifier");
			AddressAccessType.postCodeIdentifier = rs.getString("postcodeidentifier");
			AddressAccessType.districtName  = rs.getString("districtname");
			AddressAccessType.presentationString  = rs.getString("presentationstring");
			AddressAccessType.geometryWkt = rs.getString("geometrywkt");
			AddressAccessType.json = rs.getString("json");
			list.add(AddressAccessType);
		}
		rs.close();
		return list;
	}
	
	private static List<SearchResult> createStreetSearchResults(Connection connection, String select) throws Exception {
		List<SearchResult> list = new ArrayList<SearchResult>();
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(select);
		while (rs.next()){
			StreetNameType streetNameType = new StreetNameType();
			streetNameType.streetName = rs.getString("streetname");
			streetNameType.postCodeIdentifier = rs.getString("postcodeidentifier");
			streetNameType.districtName  = rs.getString("districtname");
			streetNameType.presentationString  = rs.getString("presentationstring");
			list.add(streetNameType);
		}
		return list;
	}

	public static String writeSearchResults(List<SearchResult> searchResults){
		StringBuffer out = new StringBuffer();
		for(SearchResult searchResult : searchResults) {
	        out.append(" " + searchResult.toString() + "\n");
	    }
		return out.toString();
	}
	
}
