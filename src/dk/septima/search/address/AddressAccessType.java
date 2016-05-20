package dk.septima.search.address;

public class AddressAccessType extends StreetNameType {
	String streetBuildingIdentifier = "";
	String geometryWkt = "";
	
	public String toString(){
		return "[" + streetName + "][" + streetBuildingIdentifier + "][" + postCodeIdentifier + "][" + districtName + "][" + presentationString + "]";  
	}
	
	@Override
	String getType() {
		return"addressAccessType";
	}
	
}
