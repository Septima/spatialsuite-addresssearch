package dk.septima.search.address;

public class AddressAccessType extends StreetNameType {
	String type = "AddressAccessType";
	String streetBuildingIdentifier = "";
	String geometryWkt = "";
	
	public String toString(){
		return "[" + streetName + "][" + streetBuildingIdentifier + "][" + postCodeIdentifier + "][" + districtName + "][" + presentationString + "]";  
	}
	
//  "type":"streetNameType"
//  "streetName":"Granskoven",
//	"streetBuildingIdentifier":"8",
//  "postCodeIdentifier":"2600",
//  "districtName":"Glostrup",
//  "presentationString":"Granskoven (2600 Glostrup)",
//  "geometryWkt":"POLYGON((714359 6175818,714359 6175999,714601 6175999,714601 6175818,714359 6175818))",


}
