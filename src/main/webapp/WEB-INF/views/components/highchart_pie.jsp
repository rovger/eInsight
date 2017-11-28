<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
	<script type="text/javascript" language="javascript" src="media/js/jquery.js"></script>

<html><body>

<div id="container" style="min-width: 310px; height: 400px; margin: 0 auto"></div>

<script type="text/javascript">
function share(){
	window.parent.window.open(window.location.href);
}
$(function () {
        $('#container').highcharts({
        	credits:{
        		// enabled:true,                   
        		text:'<a href="javascript:share()"">share</a>' ,
        		href:""
        	},
        	chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: '${model.title}',
                x: -20 //center
            },
            subtitle: {
                text: '${model.subTitle}',
                x: -20
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        color: '#000000',
                        connectorColor: '#000000',
                        format: '<b>{point.name}</b>: {point.percentage:.1f} %'
                    },
                    showInLegend: true

                }
            },
            series: [{
                type: 'pie',
                name: '${model.seriesTitle}',
                data:  ${model.seriesDataList}
            }]
        });
    });
    

		</script>
	<script src="js/highchart/highcharts.js"></script>
	</body>
</html>
