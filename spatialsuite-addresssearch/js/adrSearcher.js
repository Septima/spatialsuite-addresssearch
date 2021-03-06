AdrSearcher = Septima.Class (Septima.Search.Searcher,{
	
	initialize: function (options) {
		if (options == undefined){
			throw "New Septima.Search.AdrSearcher(options): Options missing.";
		}
		this.source = "adrsearcher";
		options.usesGeoFunctions = true
        Septima.Search.Searcher.prototype.constructor.apply(this, [options])
		this.type = "Adress";
        this.registerType(this.source, this.type);
	},
	
	fetchData: function (query, caller){
		var data = {
			limit: query.limit + 1
		};
		
		if (query.queryString == ""){
			data.query = "a";
		}else{
			data.query = query.queryString;
		}
		
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
	    
	},
	
	success: function(caller, data, textStatus, jqXHR){
		if (jqXHR.status == 200){
			if (data.status == "OK"){
				caller.fetchSuccess(this.getDataSearchResult(data));
			}else{
				caller.fetchError(this, data.message);
			}
		}else{
			caller.fetchError(this, jqXHR.statusText);
		}
	},
	
	getDataSearchResult: function (data){
		var queryResult = this.createQueryResult();
		var query = data.query;
		var limit = query.limit;
		var hitType;
		var result;
		var newQuery;
		if (data.numHits > 0 ){
			hitType = data.data[0].type;
		}
	    for (var i = 0; i < data.numHits && i < limit; i++){
	        var thisHit = data.data[i];
	        if (hitType == 'streetNameType'){
	        	var newQueryString = thisHit.streetName + " <select> </select> "  + thisHit.postCodeIdentifier + ' ' + thisHit.districtName;
	        	newQuery = queryResult.addNewQuery(this.source, this.type, thisHit.presentationString, null, newQueryString, null, thisHit);
	        	newQuery.image = Septima.Search.icons.road;
	        }else{
	            var resultGeometry = this.translateWktToGeoJsonObject(thisHit.geometryWkt);
	            result = queryResult.addResult(this.source, this.type, thisHit.presentationString, null, resultGeometry, thisHit);
	            result.image = Septima.Search.icons.mapPointGrey;
	        }
	    }
        if (hitType == 'streetNameType' && data.numHits > limit && !query.hasTarget){
			var description = null;
        	newQuery = queryResult.addNewQuery(this.source, this.type, "Fler Adresser", description, query.queryString, null, null);
        	newQuery.image = Septima.Search.icons.road;
        }
        if (hitType == 'addressAccessType' && data.numHits > limit && !query.hasTarget){
			var description = null;
        	result = queryResult.addNewQuery(this.source, this.type, "Fler Adresser", description, query.queryString, null, null);
            result.image = Septima.Search.icons.mapPointGrey;
        }

	    return queryResult;
	},
	
	error: function(caller, jqXHR, textStatus, errorThrown){
		caller.fetchError(this, errorThrown);
	},

	CLASS_NAME: 'AdrSearcher'

});
