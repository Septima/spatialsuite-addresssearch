package dk.septima.search.address;

public class ParseResult {
	String raw = "";
	String streetName = "";
	String streetbuildingidentifier  = "";
	String postCode = "";
	
	public boolean hasStreetName(){
		return !this.streetName.equalsIgnoreCase("");
	}
	
	public boolean hasStreetbuildingidentifier(){
		return !this.streetbuildingidentifier.equalsIgnoreCase("");
	}

	public boolean hasPostCode(){
		return !this.postCode.equalsIgnoreCase("");
	}

	public String toString(){
		return raw +": [" + streetName + "][" + streetbuildingidentifier + "][" + postCode + "]";  
	}
}
