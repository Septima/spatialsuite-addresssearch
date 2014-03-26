package dk.septima.search.address;

public class StreetNameType extends SearchResult {
		String type = "streetNameType";
		String streetName = "";
		String postCodeIdentifier = "";
		String districtName = "";
		String presentationString = "";
		
		public String toString(){
			return "[" + streetName + "][" + postCodeIdentifier + "][" + districtName + "][" + presentationString + "]";  
		}
		
//        "type":"streetNameType"
//        "streetName":"Granskoven",
//        "postCodeIdentifier":"2600",
//        "districtName":"Glostrup",
//        "presentationString":"Granskoven (2600 Glostrup)",
//        "geometryWkt":"POLYGON((714359 6175818,714359 6175999,714601 6175999,714601 6175818,714359 6175818))",
}
