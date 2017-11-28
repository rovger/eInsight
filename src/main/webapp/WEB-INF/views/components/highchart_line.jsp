<%@page import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="ISO-8859-1"%>
	<script type="text/javascript" language="javascript" src="media/js/jquery.js"></script>
	<style type="text/css">
		.label-info {
			padding: 2px 4px;
			font-size: 10px;
			color: #fff;
			background-color: #51a351;
			border-radius: 3px;
			margin:5px;
		}
	</style>
<html><body>
<div id='loadingdiv' style="font-size:28px;">Loading</div>
     <script type="text/javascript">
			var count=0;
			var interval;
			function changeloading(){
				count=count+1;
				if(count>23) {
					$('#loadingdiv').text("Loading");
				}
				var text=$('#loadingdiv').text();
				text=text+".";
				 $('#loadingdiv').text(text);
			}
			interval=setInterval(changeloading,1000);
		</script>

<div id="paramemter" style="float:right;padding-bottom:20px;">
<input type="checkbox" id="style" name="style" value="percent"/>by percent
</div>
<div id="container" style="min-width: 310px; height: 400px; margin: 0 auto"></div>


<script type="text/javascript">
function share(){
	window.parent.window.open(window.location.href);
}
function buildHighCharts(stacking){
	var ydataList = ${model.seriesDataList};
	if(stacking == 'percent') {
		for(var i=0;i<ydataList.length;i++) {
			ydataList[i].type='column';
		}
	}
	
	 $('#container').highcharts({
     	chart:{
     		zoomType:'x'
     	},
     	credits:{
     		// enabled:true,                   
     		text:'<a href="javascript:share()"">share</a>' ,
         		href:""
     	},
         title: {
             text: '${model.title}<br/><br/><br/>',
             useHTML:true,
             x: -20 //center
         },
         subtitle: {
             text: '${model.subTitle}'+ "<span class='label-info'>${model.matrix}</span>",
			 useHTML:true,
         },
         xAxis: ${model.xAixs},
         yAxis: {
             title: {
                 text: '${model.yTitle}'
             },
             labels: {
                 format: '${model.valueprefix}{value} ${model.valuesuffix}'
             },
             allowDecimals:false,
             tickmarkPlacement:'on',
             min: 0,
             maxPadding:0.3,
            
         },
         plotOptions: {
             series: {
             	animation: {
                     duration: 500,
                 },
//                 stacking: stacking,
                 marker: {
                     enabled: false
                 },
                 dataLabels: {
                   enabled: false,
                   color: '#ffffff',
                   //verticalAlign: 'top',
                    formatter: function() {
                        return this.y;
                    }
                }
             }
         },
         tooltip: {
//            pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}</b> ({point.percentage:.0f}%)<br/>',
			pointFormat: "<span style='color:{series.color}'>{series.name}</span>: <b>{point.y}</b><br/>",
            valueDecimals: ${model.valuedecimals},
            valuePrefix: '${model.valueprefix}',
            valueSuffix: '${model.valuesuffix}'
         },
         legend: {
            // align: 'center',
           //  verticalAlign: 'top',
            // floating: true
         },
         exporting: {
             enabled: true
         },
         series: ydataList
     });
}
$(function () {
	 $('#loadingdiv').hide();
	 clearInterval(interval);
	 Highcharts.setOptions({
		 global:{
		 	useUTC: false
		 }
	 });
     buildHighCharts('${model.stacking}');
    });
    

	$("#style").change(function(){
		var stacking = 'normal';
		$("[name = style]:checkbox").each(function () {
            if ($(this).is(":checked")) {
            	stacking = $(this).attr("value");
            }
        });
	    buildHighCharts(stacking);
	});
		</script>
	<script src="js/highchart/highcharts.js"></script>
	<script src="js/highchart/exporting.js"></script>
	</body>
</html>
