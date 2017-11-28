<%@ page trimDirectiveWhitespaces="true"
	contentType="text/html; charset=UTF-8"%>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>   
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="initial-scale=1.0, maximum-scale=2.0">
<!-- Bootstrap -->
<link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
<link href="css/DT_bootstrap.css" rel="stylesheet">
<link href="css/jquery.qtip.min.css" rel="stylesheet">
<link href="css/jquery.dataTables.css" rel="stylesheet">
<script type="text/javascript" language="javascript" src="js/jquery.js"></script>
<script type="text/javascript" language="javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript" language="javascript" src="js/jquery.dataTables.js"></script>
<title>eInsight</title>
	<script type="text/javascript">
		var reportNames = '';
		var domain = '${model.domain}';
		$(document).ready(function(){
			var template= $('#container #timegroup1').html();
			$('#container #timegroup1').html('');
            $.ajax({
                type: 'GET',
                url: '/eInsight/template/config/allTasks',
                contentType: 'application/json; charset=UTF-8',
                data: '',
                async: false,
                dataType: 'json',
                success: function (json) {
                    $('#wait_mask').css('display', 'none');
					if (json.taskInfo==[] || json.taskInfo.length==0) {
						$('#alert').html("No report to show, Please add it first!");
						$('#layoutselect').hide();
						return;
					}
                    $(json.taskInfo).each(function(index, value){
                        var reportName=value.taskName;
						reportNames = reportNames + ',' + reportName;
                        $('#container #timegroup1').append(template);
                        $('#container #timegroup1 #reportName a').text(reportName);
                        $('#container #timegroup1 #reportName').attr("id",reportName);
                    });
                    $("#container #timegroup1 li:first").trigger('click');
                },
                complete: function (XMLHttpRequest, textStatus) {
                },
                error: function (e) {
                }
            });
        });

		function setIframeHeight() {
			if (document.getElementById('reportContainer')) {
				var iframe = document.getElementById('reportContainer')
				var iframeWin = iframe.contentWindow || iframe.contentDocument.parentWindow;
				if (iframeWin.document.body) {
					var height = iframeWin.document.documentElement.scrollHeight || iframeWin.document.body.scrollHeight;
					$('#reportContainer').height(height + 50);
				}
			}
		}

		function changeReport(event) {
			$("#container #timegroup1 li").each(function(){
				$(this).removeClass('active');
			});
			$(event).addClass('active');
			var reportLink='reporting?reportName='+$(event).attr('id')+'&domain='+domain;
			$('#reportContainer').attr('src',reportLink);
			setIframeHeight();
		}
	</script>
</head>
<body>
	<!--/.nav-collapse -->
	<jsp:include page="header.jsp" >
	<jsp:param name="option" value="aggregateservice"/>
	</jsp:include>
	<!--/.nav-collapse -->
	<br/>
	<br/>
	<br/>
	<div class="container-fluid" id='container'>
		<div class="row-fluid">
			<div id="alert"></div>
            <div id="wait_mask">
                <img src="css/img/loading.gif">
            </div>
			<ul class="nav nav-tabs"  id="timegroup1">
			  	<li role="presentation" id='reportName' class="active" onclick="changeReport(this)" style='margin-left:10px;margin-right:10px;'><a href="javascript:void(0);"></a></li>
			</ul>
		</div>
		<div class="row-fluid">
			  <iframe  id='reportContainer' name='reportContainer' type='line' base_src='' style='width:100%;height:900px;border:0px;'></iframe>
		</div>
	</div>
</body>
</html>
