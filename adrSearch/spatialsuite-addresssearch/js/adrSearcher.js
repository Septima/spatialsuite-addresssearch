AdrSearcher = Septima.Class (Septima.Search.Searcher,{
	
	initialize: function (options) {
		if (options == undefined){
			throw "New Septima.Search.AdrSearcher(options): Options missing.";
		}
		
		options.usesGeoFunctions = true;

		this.Searcher(options);
	},
	
	fetchData: function (query, caller){
		var data = {
			query: query.queryString,
			limit: query.limit + 1
		};
	    var xhr = jQuery.ajax({
	        url: "/jsp/modules/adrsearch/query.jsp",
	        data: data,
	        jsonp: 'callback',
	        dataType: 'jsonp',
	        cache : false,
	        timeout : 10000,
	        crossDomain : true,
	        success : Septima.bind(function (caller, query, data, textStatus,  jqXHR) {
                data.query = query;
	        	this.success (caller, data, textStatus,  jqXHR);
	        },this, caller, query),
	        error : Septima.bind(function (caller, jqXHR, textStatus, errorThrown) {
	        	if (textStatus.toLowerCase() != 'abort' ){
		        	this.error (caller, jqXHR, textStatus, errorThrown);
	        	}
	        },this, caller)     
	    });
	    
	    caller.registerOnCancelHandler ( Septima.bind(function (xhr) {
	    	if (xhr && xhr.readystate != 4){
	    		xhr.abort();
	    	}
	    },this,xhr));
	},
	
	success: function(caller, data, textStatus, jqXHR){
		if (caller.isActive()){
			if (jqXHR.status == 200){
				if (data.status == "OK"){
					caller.fetchSuccess(this.getDataSearchResult(data));
				}else{
					caller.fetchError(this, data.message);
				}
			}else{
				caller.fetchError(this, jqXHR.statusText);
			}
		}
	},
	
	getDataSearchResult: function (data){
		var queryResult = this.createQueryResult();
		var query = data.query;
		var limit = query.limit;
		var hitType;
		if (data.numHits > 0 ){
			hitType = data.data[0].type;
		}
	    for (var i = 0; i < data.numHits && i < limit; i++){
	        var thisHit = data.data[i];
	        if (hitType == 'streetNameType'){
	        	var newquery = thisHit.streetName + " <select> </select> "  + thisHit.postCodeIdentifier + ' ' + thisHit.districtName;
	        	queryResult.addNewQuery(thisHit.presentationString, null, newquery, null, thisHit);
	        }else{
	            var resultGeometry = this.translateWktToGeoJsonObject(thisHit.geometryWkt);
	        	queryResult.addResult(thisHit.presentationString, null, resultGeometry, thisHit);
	        }
	    }
        if (hitType == 'streetNameType' && data.numHits > limit && !query.hasTarget){
        	var result = queryResult.addNewQuery("Fler Adresser", "Fler vägar " + this.getMatchesPhrase() +" <em>" + query.queryString + "</em>", query.queryString, null, null);
        	result.image = this.folderIconURI;
        }
        if (hitType == 'addressAccessType' && data.numHits > limit && !query.hasTarget){
        	var result = queryResult.addNewQuery("Fler Adresser", "Fler adresser " + this.getMatchesPhrase() +" <em>" + query.queryString + "</em>", query.queryString, null, null);
        	result.image = this.folderIconURI;
        }

	    return queryResult;
	},
	
	error: function(caller, jqXHR, textStatus, errorThrown){
		caller.fetchError(this, errorThrown);
	},

	CLASS_NAME: 'AdrSearcher'

});
