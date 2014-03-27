package dk.septima.search.address;

public class StreetNameType extends SearchResult {
		String streetName = "";
		String postCodeIdentifier = "";
		String districtName = "";
		String presentationString = "";
		
		public String toString(){
			return "[" + streetName + "][" + postCodeIdentifier + "][" + districtName + "][" + presentationString + "]";  
		}

		@Override
		String getType() {
			return"streetNameType";
		}
}
