<%@ page trimDirectiveWhitespaces="true"
	contentType="text/html; charset=UTF-8"%>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>   
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="initial-scale=1.0, maximum-scale=2.0">
<title>eInsight</title>
	<script type="text/javascript">
		var serviceTime = '${model.currentTime}';
		var reportName='${model.reportName}';
		var reportTitle='${model.reportTitle}';
		if(reportTitle==''){
			reportTitle=reportName;
		}
		var sharefrom = '${model.from}';
		var shareto = '${model.to}';

		function displayChart(_timelist, reportName) {
			$("#" + reportName + ' #notfound1').hide();
			$("#" + reportName + ' #datatable1').show();
			$("#" + reportName + ' #datatable2').show();
			$("#" + reportName + ' #showTimeMatrix').show();

			var isrange = false;
			var timelist = _timelist;
			//could be hour, day, week
			var reportDataTimeMatrix = 'default';
			var url, datematrix, startdateoffset, enddateoffset;
			if (timelist.length == 2) isrange = true;
			if (isrange) {
				url = '/eInsight/template/report/findbytimerange/' + reportName + '/' + timelist[0] + '/' + timelist[1] + '/' + preDays + '/0';
			} else {
				datematrix = timelist[0];
				startdateoffset = timelist[1];
				enddateoffset = timelist[2];
				reportDataTimeMatrix = timelist[3];
				url = '/eInsight/template/report/findbytimedetail/' + reportName + '/' + datematrix + '/' + startdateoffset + '/' + enddateoffset + '/0/' + reportDataTimeMatrix;
			}
			var aaData = [];
			var aaColumns = [];
			var pieData = [];
			$('#iframeMask').css('display', 'block');
			$.ajax({
				type: 'GET',
				url: url,
				contentType: 'application/json; charset=UTF-8',
				data: '',
				async: false,
				dataType: 'json',
				success: function (json)
				{
					var piemap = {};
					var totalcount = 0;
					//range default value:
					$("#fromtimepicker").val(json.start);
					$("#totimepicker").val(json.end);
					var rangematrix = json.summary.queryCondition.TimeMatrix;
					$(json.detail).each(function (index, jsonobj) {
						totalcount = totalcount + jsonobj.Count;
						if (piemap[jsonobj.ColumnValue]) {
							piemap[jsonobj.ColumnValue] = (100 * piemap[jsonobj.ColumnValue] + 100 * jsonobj.Count).toFixed(0) / 100;
						} else {
							piemap[jsonobj.ColumnValue] = jsonobj.Count;
						}
					});
					var piedetail = [];
					for (var key in piemap) {
						var pieobj = {};
						pieobj['ColumnValue'] = key;
						pieobj['Count'] = piemap[key];
						piedetail.push(pieobj);
					}
					var data = getSeriesDataList(piedetail, reportName, 'showRowData');
					aaColumns = data.columns;
					pieData = data.piedata;
					aaData = data.tdata;

					//reset the table title means.
					aaColumns[0].title = 'Value';
					aaColumns[1].title = 'Count';

					var _aadata = JSON.stringify(aaData);
					var _aacolumns = JSON.stringify(aaColumns);
					$("#" + reportName + ' #showDatatable1Form .aadata').val(_aadata);
					$("#" + reportName + ' #showDatatable1Form .aacolumns').val(_aacolumns);
					$("#" + reportName + ' #showDatatable1Form').submit();

					var _queryTypeList = timelist;
					var _timeSpan = 12;
					var offset = _queryTypeList[1];
					if(!isrange) {
						if (_queryTypeList[0] == 'hour' && _queryTypeList[1] >= -2) {
							_timeSpan = 60 * parseInt(_queryTypeList[1]);
						} else if (_queryTypeList[0] == 'hour' && _queryTypeList[1] < -2) {
							_timeSpan = parseInt(_queryTypeList[1]);
						}else if (_queryTypeList[0] == 'day') {
							_timeSpan = parseInt(_queryTypeList[1]);
						} else if (_queryTypeList[0] == 'week') {
							_timeSpan = 7 * parseInt(_queryTypeList[1]);
						} else if (_queryTypeList[0] == 'month') {
							_timeSpan = 30 * parseInt(_queryTypeList[1]);
						}
					}else{
                        datematrix = rangematrix.toLowerCase();
                        offset = json.rangeOffset;
                        _timeSpan = offset;
					}
					var timeSpan = Math.abs(_timeSpan);
					var charturi = getDataForCharts(json, datematrix, timeSpan, offset, 'area', reportTitle);
					if (charturi == '') {
						$("#" + reportName + ' #notfound2').show();
						$("#" + reportName + ' #showTimeMatrix').hide();
					} else {
						$("#" + reportName + ' #showTimeMatrixForm .title').val(reportTitle);
						$("#" + reportName + ' #showTimeMatrixForm .subTitle').val(charturi.subTitle);
						$("#" + reportName + ' #showTimeMatrixForm .seriesDataList').val(charturi.seriesDataList);
						$("#" + reportName + ' #showTimeMatrixForm .xAixs').val(charturi.xAixs);
                        $("#" + reportName + ' #showTimeMatrixForm .matrix').val(datematrix);
						$("#" + reportName + ' #showTimeMatrixForm').submit();
						//$('#showTimeMatrix').attr('src', charturi);
					}
		            $('#iframeMask').css('display', 'none');
				},
				complete: function (XMLHttpRequest, textStatus) {
				},
				error: function (e) {
					$("#" + reportName + ' #notfound1').show();
					$("#" + reportName + ' #datatable1').hide();
                    $("#" + reportName + ' #notfound1').html("<div class='alert alert-error'><strong>Error!</strong> Loading failure, Please try it again.</div>");
				}
			});
		}
	</script>
</head>
<body>
	<!--/.nav-collapse -->
	<div class="container-fluid" id='container'>
		<div id="iframeMask" style="display:block;margin-right:20px;background-color:#FFF;width:98%;position:absolute;height:800px;opacity:0.5;z-index:1;">
			<div style="margin: 15% 45%">
				<img src="css/img/loading_mask.gif"/>
			</div>
		</div>
		<div class="row-fluid template" id='reportName'>
			<div class="row-fluid">
				<h2 class="text-center"><a href="javascript:void(0)" target='_blank' style="cursor: pointer" id='reportTitle'></a></h2>
			</div>
			<div class="row-fluid">
				<div class="form-inline">
					<label class="radio">Quick:</label>
					<select id="timegap" style="margin-right: 5%">
						<option value="hour_-1_1_minute">Last 2 hours</option>
						<option value="hour_-24_0_hour" selected>Last 24 hours</option>
						<option value="hour_-96_0_hour">Last 4 days</option>
						<option value="hour_-168_0_hour">Last 1 week</option>
						<option value="day_-30_0_day">Last 1 month</option>
						<option value="day_-180_0_day">Last 6 months</option>
						<option value="day_-365_0_day">Last 1 year</option>
					</select>
					<label class="radio">Range:</label>
					<div class="input-prepend">
						<span class="add-on">From</span>
						<input id="fromtimepicker" class="timepicker" type="text" placeholder="StartTime" data-date-format="">
						<span class="add-on">To</span>
						<input id="totimepicker" class="timepicker" type="text" placeholder="EndTime" data-date-format="">
					</div>
					<button class="btn" type="button" id="refresh" onclick="changeGap(0)">Refresh</button>
					<div class="nav-collapse collapse pull-right">
						<ul class="nav" style="cursor: pointer;">
							<li style="float: left; padding-right: 15px;"><a onclick="changeGap(-7)"><strong>Prev Week</strong></a></li>
							<li style="float: left; padding-right: 15px;"><a onclick="changeGap(-1)"><strong>Prev Day</strong></a></li>
							<li style="float: left; padding-right: 15px;"><a onclick="changeGap(1)"><strong>Next Day</strong></a></li>
							<li style="float: left; padding-right: 15px;"><a onclick="changeGap(7)"><strong>Next Week</strong></a></li>
						</ul>
					</div>
				</div>
			</div>
			
			<div class="row-fluid">
				<div class="span5" style='border:1px solid #ccc;padding:20px 0px;'>
					<div class="row-fluid">
						<div id='notfound1' style='height: 60px; padding-top: 30px; font-size: 16px;display:none'></div>
					    <form action="datatables_common" method='POST' target='datatable1' id='showDatatable1Form'>
					  		<input type='hidden' class='aadata' name='aadata'>
					  		<input type='hidden' class='aacolumns' name='aacolumns'>
				        </form>
						<iframe id='datatable1' name='datatable1' type='dt' base_src='datatables_common?' style='width:100%;height:500px;border:0px;'></iframe>
					</div>
				</div>
				<div class="span7" style='border:1px solid #ccc;padding:20px 0px;'>
					<div class="row-fluid">
						 <div id='notfound2' style='text-align: center;height: 60px; font-size: 26px; padding-top: 30px;display:none'></div>
						 <form action="highchart_line" method='POST' target='showTimeMatrix' id='showTimeMatrixForm'>
					  	<input type='hidden' class='title' name='title'>
					  	<input type='hidden' class='subTitle' name='subTitle'>
					  	<input type='hidden' class='seriesDataList' name='seriesDataList'>
					  	<input type='hidden' class='xAixs' name='xAixs'>
						<input type='hidden' class='matrix' name='matrix'>
					  </form>
					  <iframe  id='showTimeMatrix' name='showTimeMatrix' type='line' base_src='highchart_line' style='width:100%;height:500px;border:0px;'></iframe>
					</div>
				 </div>
			 </div>
		</div>
	</div>
</body>
<!-- Bootstrap -->
<link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
<link href="css/DT_bootstrap.css" rel="stylesheet">
<link href="css/jquery.qtip.min.css" rel="stylesheet">
<link href="css/jquery.dataTables.css" rel="stylesheet">
<link href="css/datetimepicker.css" rel="stylesheet"/>
<script type="text/javascript" language="javascript" src="js/jquery.js"></script>
<script type="text/javascript" language="javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript" language="javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" language="javascript" src="js/bootstrap-datetimepicker.min.js"></script>
<script type="text/javascript" language="javascript" src="js/chartAdapter/reporting_pie.js"></script>
<script type="text/javascript" language="javascript" src="js/chartAdapter/reporting_line.js"></script>
<script type="text/javascript" language="javascript" src="js/timegap.js?<%=System.currentTimeMillis()%>"></script>
</html>
