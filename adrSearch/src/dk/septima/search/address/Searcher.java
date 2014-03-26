package dk.septima.search.address;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Searcher {
	
	public static List<SearchResult> search(String input, Connection connection, int maxResults) throws Exception{
		
		ParseResult parseResult = Parser.parse(input);
		String select = "";
		String streetSelect = "select * from adrsearch.streetname";
		String adrSelect = "select * from adrsearch.addressaccess";
		String clause = "  ";
		String streetOrderClause = " order by streetName, postcodeidentifier";
		String addressOrderClause = " order by streetName, postcodeidentifier, sortorder";
		String limitClause = " limit " + Integer.toString(maxResults) + " ";
		if (parseResult.hasStreetName()){
			clause += " where streetname ilike '" + parseResult.streetName + "%' "; 
			if (parseResult.hasPostCode()){
				clause += " and postcodeidentifier = '" + parseResult.postCode + "' "; 
			}
			if (parseResult.hasStreetbuildingidentifier()){
				clause += " and streetbuildingidentifier ilike '" + parseResult.streetbuildingidentifier + "%' "; 
				select = adrSelect + clause + addressOrderClause + limitClause;
				return createAddressSearchResults(connection, select);
			}else{
				select = streetSelect + clause + streetOrderClause + limitClause;
				List<SearchResult> searchResults = createStreetSearchResults(connection, select);
				if (searchResults.size() > 1){
					return searchResults;
				}else{
					select = adrSelect + clause + addressOrderClause + limitClause;
					return createAddressSearchResults(connection, select);
				}
			}

		}else{
			if (parseResult.hasPostCode()){
				clause += " where postcodeidentifier = '" + parseResult.postCode + "' "; 
				if (parseResult.hasStreetbuildingidentifier()){
					clause += " and streetbuildingidentifier ilike '" + parseResult.streetbuildingidentifier + "%' "; 
					select = adrSelect + clause + addressOrderClause + limitClause;
					return createAddressSearchResults(connection, select);
				}else{
					select = streetSelect + clause + streetOrderClause + limitClause;
					List<SearchResult> searchResults = createStreetSearchResults(connection, select);
					if (searchResults.size() > 1){
						return searchResults;
					}else{
						select = adrSelect += clause + addressOrderClause + limitClause;
						return createAddressSearchResults(connection, select);
					}
				}
			}else{
				if (parseResult.hasStreetbuildingidentifier()){
					clause += " and streetbuildingidentifier ilike '" + parseResult.streetbuildingidentifier + "%' "; 
					select = adrSelect + clause + addressOrderClause + limitClause;
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
			AddressAccessType.geometryWkt  = rs.getString("geometrywkt");
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

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Connection connection = getConnection();
		int limit = 20;
		test("444 94 Ucklum", connection, limit);
		test("Amdal 123 444 94 Ucklum", connection, limit);
		test("Amdal 123 444 94", connection, limit);
		test("Amdal 123", connection, limit);
		test("Am 123", connection, limit);
		test("Am 1", connection, limit);
		test("Am 2", connection, limit);
		test("Am", connection, limit);
		test("A", connection, limit);
		test("A 444 94", connection, limit);
		test("Amdal 444 94 Ucklum", connection, limit);
		test("Ainas väg", connection, limit);
		test("Amdal 123 (444 94 Ucklum)", connection, limit);
		test("Ainas väg 123", connection, limit);
	}
	
	public static void test(String input, Connection connection, int maxResults) throws Exception{
		System.out.println(input + ": ");
		System.out.println(writeSearchResults(Searcher.search(input, connection, maxResults)));
	}
	
	public static String writeSearchResults(List<SearchResult> searchResults){
		StringBuffer out = new StringBuffer();
		for(SearchResult searchResult : searchResults) {
	        out.append(" " + searchResult.toString() + "\n");
	    }
		return out.toString();
	}
	
	private static Connection getConnection() throws Exception{
		   // JDBC driver name and database URL
		   String JDBC_DRIVER = "org.postgresql.Driver";  
		   String DB_URL = "jdbc:postgresql://localhost/adrsearch";

		   //  Database credentials
		   String USER = "postgres";
		   String PASS = "postgres";
		   
		   Connection connection = DriverManager.getConnection(DB_URL,USER,PASS);
		 
		return connection;
		
	}

}
