/* param json:      data from rest service
	 * param timeSpan:  number of dots on chart
	 * param chartType: could be line , area or percent
	 * 
	 * get data list of series of Highcharts
	 * 
	 * */
	function getDataForConversionRate(currentJson, historyJson, datematrix, timeSpan, chartType, reportpath) {
		var curRateData = [];
		var hisRateData = [];
		var YList=[];
		var typeList = [];
		var YData_cur = [];
		var YData_his = [];
		var rateList=['Conversion_Rate', 'Conversion_Rate--1 week ago'];
		var _pointInterval=0;
		var _starttime = 0;
		var subTitle = '';
		if(currentJson.start_time && currentJson.end_time) {
			subTitle = currentJson.start_time + ' - ' + currentJson.end_time
		}
		if(currentJson.summary.queryCondition.StartDate.$gte) {
			var dateString = currentJson.summary.queryCondition.StartDate.$gte;
			_starttime = new Date(dateString).getTime();
		}

		if (currentJson.detail.length > 0) {
			for(var i = 0; i < currentJson.detail.length; i++) {
				var type = currentJson.detail[i].ColumnValue;
				if(type != 'null') {
					if(JSON.stringify(typeList).indexOf(JSON.stringify(type)) == -1) {
						typeList.push(type);
					}
				}
			}
		}else {
			return '';
		}

		if(datematrix == 'minute') {
			_pointInterval = 60*1000;
		}else if (datematrix == 'hour') {
			_pointInterval = 60*60*1000;
		}else if(datematrix == 'day') {
			_pointInterval = 24*60*60*1000;
		}else if(datematrix == 'month' || datematrix == 'week') {
			_pointInterval = 7*24*60*60*1000;
		}
		var timeline = [];

		for(var i = 0; i <= timeSpan; i++) {
			timeline[i] = _starttime + i*_pointInterval;
            //timeline_typelist[][]
            curRateData[i] = new Array(typeList.length);
            hisRateData[i] = new Array(typeList.length);
		}

		for(var j = 0; j < typeList.length; j++) {
			var timelineSize = timeline.length;
			YData_cur[j] = new Array(timelineSize);
			YData_his[j] = new Array(timelineSize);
			for(var k = 0; k < timelineSize; k++) {
				YData_cur[j][k] = 0;
				YData_his[j][k] = 0;
			}
		}

		//current
		for(var i = 0; i < currentJson.detail.length; i++) {
			for(var k = 0; k < typeList.length; k++) {
				if (currentJson.detail[i].ColumnValue == typeList[k]) {
					var dotStartDate = new Date(currentJson.detail[i].StartDate);
					var dot_Start_utc_timestamp = dotStartDate.getTime();

					var dotEndDate = new Date(currentJson.detail[i].EndDate);
					var dot_End_utc_timestamp = dotEndDate.getTime();

					for(var j=0; j < timeline.length; j++ ) {
						if(dot_Start_utc_timestamp <= timeline[j] && timeline[j] < dot_End_utc_timestamp ) {
							YData_cur[k][j] = currentJson.detail[i].Count;
                            //rateData assign operation.
                            curRateData[j][k] = YData_cur[k][j];
						}
					}
				}
			}
		}

		//history
		for (var i=0; i<historyJson.detail.length; i++) {
			for (var k=0; k<typeList.length; k++) {
				if (historyJson.detail[i].ColumnValue==typeList[k]) {
					var weekTimeGap = 7*24*60*60*1000;
					var dotStartDate = new Date(historyJson.detail[i].StartDate);
					var dot_start_utc_timestamp = dotStartDate.getTime()+weekTimeGap;

					var dotEndDate = new Date(historyJson.detail[i].EndDate);
					var dot_end_utc_timestamp = dotEndDate.getTime()+weekTimeGap;

					for (var j=0; j<timeline.length; j++) {
						if (dot_start_utc_timestamp<=timeline[j] && timeline[j]<dot_end_utc_timestamp) {
							YData_his[k][j] = historyJson.detail[i].Count;
							//
							hisRateData[j][k] = YData_his[k][j];
						}
					}
				}
			}
		}

		//calculate conversion_rate[]
        var rates = new Array(new Array(timeline.length), new Array(timeline.length));
        for(var i=0; i<timeline.length; i++) {
            var sum=0, max=0;
            for(var j=0; j<typeList.length; j++) {
				if(!curRateData[i][j]) continue;
				var curr = parseInt(curRateData[i][j]);
                sum = sum + curr;
                if(curr>max) max=curr;
            }
            if(max==0) continue;
			rates[0][i] = parseFloat((Math.round((sum-max)/max*10000)/100.00).toFixed(1));
        }
        for(var i=0; i<timeline.length; i++) {
            var sum=0, max=0;
            for(var j=0; j<typeList.length; j++) {
                if(!hisRateData[i][j]) continue;
                var curr = parseInt(hisRateData[i][j]);
                sum = sum + curr;
                if(curr>max) max=curr;
            }
            if(max==0) continue;
            rates[1][i] = parseFloat((Math.round((sum-max)/max*10000)/100.00).toFixed(1));
        }

        for(var i=0; i<rateList.length; i++) {
        	var hisColor;
        	if(i==1) hisColor= 'red';
            YList.push({name:rateList[i],data:rates[i],type:chartType, pointInterval:_pointInterval, pointStart: _starttime, color: hisColor});
		}

		//draw the chart
		var _dateTimeLabelFormats = {second: '%H:%M:%S',minute: '%H:%M',hour: '%H:%M',day: '%e th',week: '%e'};
		var _xAixs = {type: 'datetime', dateTimeLabelFormats:_dateTimeLabelFormats, minTickInterval:_pointInterval};
		var _x = JSON.stringify(_xAixs);

		var _title = reportpath;
		var _subTitle = subTitle;
		var _seriesDataList = JSON.stringify(YList);

		var chart={};
		chart.title=_title;
		chart.subTitle=_subTitle;
		chart.seriesDataList=_seriesDataList;
		chart.xAixs=_x;
		return chart;
	}

	function getDataForCharts(json, datematrix, timeSpan, startdateoffset, chartType, reportpath) {
		var chartData = [];
		var YList=[];
		var typeList = [];
		var YData = [];
		var xTickList=[];
		var _pointInterval=0;
		var _starttime = 0;
		var subTitle = '';
		if(json.start_time && json.end_time) {
			subTitle = json.start_time + ' - ' + json.end_time
		}
		if(json.summary.queryCondition.StartDate.$gte) {
			var dateString = json.summary.queryCondition.StartDate.$gte;
			_starttime = new Date(dateString).getTime();
		}
		
		if (json.detail.length > 0) {
			for(var i = 0; i < json.detail.length; i++) {
				var type = json.detail[i].ColumnValue;
				if(type != 'null') {
					if(JSON.stringify(typeList).indexOf(JSON.stringify(type)) == -1) {
						typeList.push(type);
					}
				}
			}
		}else {
			return '';
		}

		if(datematrix == 'minute') {
			_pointInterval = -startdateoffset*60*1000/timeSpan;
		}else if (datematrix == 'hour') {
			_pointInterval = -startdateoffset*60*60*1000/timeSpan;
		}else if(datematrix == 'day') {
			_pointInterval = -startdateoffset*24*60*60*1000/timeSpan;
		}else if(datematrix == 'month' || datematrix == 'week') {
			_pointInterval = -startdateoffset*7*24*60*60*1000/timeSpan;
		}
		var timeline = [];
		
		for(var i = 0; i <= timeSpan; i++) {
			timeline[i] = _starttime + i*_pointInterval;
		}
		
		for(var j = 0; j < typeList.length; j++) {
			var timelineSize = timeline.length;
			YData[j] = new Array(timelineSize);
			for(var k = 0; k < timelineSize; k++) {
				YData[j][k] = 0;
			}
		}
		
		for(var i = 0; i < json.detail.length; i++) {
			for(var k = 0; k < typeList.length; k++) {
				if (json.detail[i].ColumnValue == typeList[k]) {
					var dotStartDate = new Date(json.detail[i].StartDate);
					var dot_Start_utc_timestamp = dotStartDate.getTime();
					
					var dotEndDate = new Date(json.detail[i].EndDate);
					var dot_End_utc_timestamp = dotEndDate.getTime();
					
					for(j=0; j < timeline.length; j++ ) {
						if(dot_Start_utc_timestamp <= timeline[j] && timeline[j] < dot_End_utc_timestamp ) {
							YData[k][j] = json.detail[i].Count;
						}
					}
				}
			}
		}
		
		for(var j = 0; j < typeList.length; j++) {
			YList.push({name:typeList[j],data:YData[j],type:chartType, pointInterval:_pointInterval, pointStart: _starttime});
		}
		
		//draw the chart
		var _dateTimeLabelFormats = {second: '%H:%M:%S',minute: '%H:%M',hour: '%H:%M',day: '%e th',week: '%e'};
		var _xAixs = {type: 'datetime', dateTimeLabelFormats:_dateTimeLabelFormats, minTickInterval:60*1000};
		var _x = JSON.stringify(_xAixs);
		
		var _title = reportpath;
		var _subTitle = subTitle;
		var _seriesDataList = JSON.stringify(YList);
		
		var chart={};
		chart.title=_title;
		chart.subTitle=_subTitle;
		chart.seriesDataList=_seriesDataList;
		chart.xAixs=_x;
		return chart;
	}
	
	function getDataURLForCharts(json, datematrix, timeSpan, startdateoffset, chartType, reportpath) {
		var chartData = [];
		var YList=[];
		var typeList = [];
		var YData = [];
		var xTickList=[];
		var _pointInterval=0;
		var _starttime = 0;
		var subTitle = '';
		if(json.start_time && json.end_time) {
			subTitle = json.start_time + ' - ' + json.end_time
		}
		if(json.summary.queryCondition.StartDate.$gte) {
			var dateString = json.summary.queryCondition.StartDate.$gte;
			_starttime = new Date(dateString).getTime();
		}
		
		if (json.detail.length > 0) {
			for(var i = 0; i < json.detail.length; i++) {
				var type = json.detail[i].ColumnValue;
				if(type != 'null') {
					if(JSON.stringify(typeList).indexOf(JSON.stringify(type)) == -1) {
						typeList.push(type);
					}
				}
			}
		}else {
			return '';
		}
		
		if (datematrix == 'hour') {
			_pointInterval = -startdateoffset*60*60*1000/timeSpan;
		}else if(datematrix == 'day') {
			_pointInterval = -startdateoffset*24*60*60*1000/timeSpan;
		}else if(datematrix == 'month' || datematrix == 'week') {
			_pointInterval = -startdateoffset*7*24*60*60*1000/timeSpan;
		}
		var timeline = [];
		
		for(var i = 0; i <= timeSpan; i++) {
			timeline[i] = _starttime + i*_pointInterval;
		}
		
		for(var j = 0; j < typeList.length; j++) {
			var timelineSize = timeline.length;
			YData[j] = new Array(timelineSize);
			for(var k = 0; k < timelineSize; k++) {
				YData[j][k] = 0;
			}
		}
		
		for(var i = 0; i < json.detail.length; i++) {
			for(var k = 0; k < typeList.length; k++) {
				if (json.detail[i].ColumnValue == typeList[k]) {
					var dotStartDate = new Date(json.detail[i].StartDate);
					var dot_Start_utc_timestamp = dotStartDate.getTime();
					
					var dotEndDate = new Date(json.detail[i].EndDate);
					var dot_End_utc_timestamp = dotEndDate.getTime();
					
					for(j=0; j < timeline.length; j++ ) {
						if(dot_Start_utc_timestamp <= timeline[j] && timeline[j] < dot_End_utc_timestamp ) {
							YData[k][j] = json.detail[i].Count;
						}
					}
				}
			}
		}
		
		for(var j = 0; j < typeList.length; j++) {
			YList.push({name:typeList[j],data:YData[j],type:chartType, pointInterval:_pointInterval, pointStart: _starttime});
		}
		
		//draw the chart
		var _dateTimeLabelFormats = {second: '%H:%M:%S',minute: '%H:%M',hour: '%H:%M',day: '%e th',week: '%e'};
		var _xAixs = {type: 'datetime', dateTimeLabelFormats:_dateTimeLabelFormats, minTickInterval:60*1000};
		var _x = encodeURI(JSON.stringify(_xAixs));
		
		var _title = encodeURI(reportpath);
		var _subTitle = encodeURI(subTitle);
		var _seriesDataList = encodeURI(JSON.stringify(YList));
		
		var url = 'highchart_line?title=' + _title + '&subTitle=' + _subTitle + '&seriesDataList='+_seriesDataList+ '&xAixs=' + _x;
		return url;
	}
	
	function getData(json, datematrix, timeSpan, startdateoffset, chartType, historyDatematrix) {
		var YList=[];
		var typeList = [];
		var YData = [];
		var _pointInterval=0;
		var _starttime = 0;
		var subTitle = '';
		if(json.start_time && json.end_time) {
			subTitle = json.start_time + ' - ' + json.end_time
		}
		if(json.summary.queryCondition.StartDate.$gte) {
			var dateString = json.summary.queryCondition.StartDate.$gte;
			_starttime = new Date(dateString).getTime();
		}
		
		if (json.detail.length > 0) {
			for(var i = 0; i < json.detail.length; i++) {
				var type = json.detail[i].ColumnValue;
				if(type != 'null') {
					if(JSON.stringify(typeList).indexOf(JSON.stringify(type)) == -1) {
						typeList.push(type);
					}
				}
			}
		}else {
			return '';
		}
		
		if (datematrix == 'hour') {
			_pointInterval = -startdateoffset*60*60*1000/timeSpan;
		}else if(datematrix == 'day') {
			_pointInterval = -startdateoffset*24*60*60*1000/timeSpan;
		}else if(datematrix == 'month' || datematrix == 'week') {
			_pointInterval = -startdateoffset*7*24*60*60*1000/timeSpan;
		}
		var timeline = [];
		
		for(var i = 0; i <= timeSpan; i++) {
			timeline[i] = _starttime + i*_pointInterval;
		}
		
		for(var j = 0; j < typeList.length; j++) {
			var timelineSize = timeline.length;
			YData[j] = new Array(timelineSize);
			for(var k = 0; k < timelineSize; k++) {
				YData[j][k] = 0;
			}
		}
		
		for(var i = 0; i < json.detail.length; i++) {
			for(var k = 0; k < typeList.length; k++) {
				if (json.detail[i].ColumnValue == typeList[k]) {
					var dotStartDate = new Date(json.detail[i].StartDate);
					var dot_Start_utc_timestamp = dotStartDate.getTime();
					
					var dotEndDate = new Date(json.detail[i].EndDate);
					var dot_End_utc_timestamp = dotEndDate.getTime();
					
					for(j=0; j < timeline.length; j++ ) {
						if(dot_Start_utc_timestamp <= timeline[j] && timeline[j] < dot_End_utc_timestamp ) {
							YData[k][j] = json.detail[i].Count;
						}
					}
				}
			}
		}
		
		for(var j = 0; j < typeList.length; j++) {	
			YList.push({name:typeList[j],data:YData[j],type:chartType, pointInterval:_pointInterval, pointStart: _starttime});
		}
		return {subTitle:subTitle, dataList:YList, matrix:datematrix};
	}
	
	function getComparisonCharts(currentChartData, historyChartData, reportName, funcName) {
		
		var charts = [];
		var _subTitle = '';
		var yListData = [];
		if(currentChartData.dataList) {
			_subTitle = currentChartData.subTitle;
			yListData = currentChartData.dataList;
		}
		var _dateTimeLabelFormats = {second: '%H:%M:%S',minute: '%H:%M',hour: '%H:%M',day: '%e th',week: '%e'};
		var _xAixs = {type: 'datetime', dateTimeLabelFormats:_dateTimeLabelFormats, minTickInterval:60*1000};
		var _x = encodeURI(JSON.stringify(_xAixs));
		var _subTitle = encodeURI(_subTitle);
		
		for(var i = 0; i < yListData.length; i++) {
			var YList=[];
			var typeName = yListData[i].name;
			var yArray = yListData[i].YData;
			var _pointInterval = yListData[i].pointInterval;
			var _starttime = yListData[i].pointStart;
			
			if(historyChartData.dataList) {
				for(var j = 0; j < historyChartData.dataList.length; j++) {
					if(typeName == historyChartData.dataList[j].name) {
						//add history y data array
						YList.push({name:typeName + '--1 week ago',data:historyChartData.dataList[j].data,type:historyChartData.dataList[j].type, pointInterval:_pointInterval, pointStart: _starttime,color:'red'});
					}
				}
			}
			
			YList.push(yListData[i]);
			var _title ='<a href="javascript:parent.'+funcName + '(%27' + reportName + '%27,%27' + encodeURI(typeName) + '%27);" style="text-decoration:none" title="click to query">'+ typeName + '</a>';
			var _seriesDataList = encodeURI(JSON.stringify(YList));
			var url = 'highchart_line?title=' + encodeURIComponent(_title) + '&subTitle=' + _subTitle + '&seriesDataList='+_seriesDataList+ '&xAixs=' + _x+ '&matrix='+ currentChartData.matrix;
			charts.push(url);
		}
		
		return charts;
		
	}
	