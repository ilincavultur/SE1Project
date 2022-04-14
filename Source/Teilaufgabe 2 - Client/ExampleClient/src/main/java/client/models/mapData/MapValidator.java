package client.models.mapData;

public class MapValidator {

	

	public boolean hasFort(ClientMap mapToVerify) {
		return true;
	}
	
	public boolean hasNoIsland(ClientMap mapToVerify) {
		return true;
	}
	
	public boolean verifyNoOfFields(ClientMap mapToVerify) {
		return true;
	}
	
	public boolean verifyFieldTypesNo(ClientMap mapToVerify) {
		return true;
	}
	
	public boolean validateMap(ClientMap myMap) {
		if ( hasFort(myMap) && hasNoIsland(myMap) && verifyNoOfFields(myMap) && verifyFieldTypesNo(myMap) ) {
			return true;
		}else {
			return false;	
		}
		
	}
	
}
