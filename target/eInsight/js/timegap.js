var preDays = 0;
$(function () {
	//time picker initialized.
	$(".timepicker").datetimepicker({
		format: "yyyy-mm-ddThh:ii",
		autoclose: true,
		minView: 0,
		startDate: "2012-01-01T00:00",
		endDate: serviceTime,
		initialDate: "2012-01-01T00:00",
		minuteStep: 5,
		minView: 1
	});
	$('#container #reportName').attr("id",reportName);
	$('#container #'+reportName+ ' #reportTitle').text(reportTitle);
	//radio elements initialized
	if(sharefrom!='' && shareto!='') {
		//range default value:
		$("#fromtimepicker").val(sharefrom);
		$("#totimepicker").val(shareto);
		var timestring = sharefrom + ',' + shareto;
		displayChart(timestring.split(","), reportName);
	} else {
        $('#container .template').each(function () {
            var timelist = $("#timegap").val().split('_');
            displayChart(timelist, reportName);
        });
    }
	//quickly show
	$("#timegap").change(function(){
        var timelist=$("#timegap").val().split('_');
        displayChart(timelist , reportName);
	});
});
var changeGap = function (offset) {
	preDays = offset;
	var startTime = $("#fromtimepicker").val();
	var endTime = $("#totimepicker").val();
	if(!endTime || !startTime || startTime>endTime){
		return;
	};
	var timestring=startTime+","+endTime;
	displayChart(timestring.split(","), reportName);
}
var showRowData = function(columnname,columnvalue) {
    var url= '/eInsight/template/report/rawdatas/'+ columnname + '/' + encodeURI(columnvalue) +'/'+ $("#fromtimepicker").val() +'/'+ $("#totimepicker").val() +'/10';
    //var url='/eInsight/template/report/rawdatas/'+ columnname + '/' + encodeURI(columnvalue) +'/'+ formatDateTime(start) +'/'+ formatDateTime(end) +'/10';
    window.open('showDatatables?url='+encodeURIComponent(url));
}