function getSeriesDataList(jsonData,reportpath, funcName) {
		var count = 0;
		var tdata=[];
		var piedata = [];
		var columns=[];
		var totalCount=0;
		var column1 = {"title":"ColumnValue","class": "center" };
		var column2 = {"title":"Count","class": "center" };
		columns.push(column1);
		columns.push(column2);
		 $.each(jsonData,function(index,jsonobj){
			 var aData=[];
			 var pData = [];
			 $.each(jsonobj,function(key,value)
			   {
				 if(key == 'ColumnValue' || key == 'Count') {
					 if(value == 'null') {
						return false;
					 }
					 if(typeof value == 'object'){
						 $.each(value,function(kk, val){
							 if(val==null){
								 val="null";
							 }
							 aData.push('<a href="javascript:parent.'+funcName + '(\'' + reportpath + '\',\'' + encodeURI(val) + '\');">'+ val + '</a>');
							 pData.push(val);
						 });
					 }else {
						 if(value==null){
							 value="null";
						 }
						 if(key == 'ColumnValue') {
							 aData.push('<a href="javascript:parent.'+funcName + '(\'' + reportpath + '\',\'' + encodeURI(value) + '\');">'+ value + '</a>');
						 }else {
							 totalCount=(100*totalCount+100*value).toFixed(0)/100;
							 aData.push(value);
						 }
						 
						 pData.push(value);
					 }
				 }
			 });
			 
			 if(aData.length != 0) {
				 tdata.push(aData);
			 }
			 if(pData.length != 0) {
				 piedata.push(pData);
			 }
		 });
		 /*var totalData=[];
		 totalData.push("<strong>TotalCount:</strong>");
		 totalData.push(totalCount);
		 tdata.push(totalData);*/
		 return {tdata:tdata, piedata:piedata,columns:columns};
	}
