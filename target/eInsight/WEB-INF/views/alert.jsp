<%@ page trimDirectiveWhitespaces="true"
	contentType="text/html; charset=UTF-8"%>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>   
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Alert</title>
<meta name="viewport" content="initial-scale=1.0, maximum-scale=2.0">
<!-- Bootstrap -->
<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="css/bootstrap-responsive.min.css">
<link rel="stylesheet" type="text/css" href="css/DT_bootstrap.css">
<link rel="stylesheet" type="text/css" href="css/jquery.qtip.min.css">
<link rel="stylesheet" type="text/css" href="media/css/jquery.dataTables.css">
<link rel="stylesheet" type="text/css" href="css/jquery.jsonview.css">
<script type="text/javascript" language="javascript" src="media/js/jquery.js"></script>
<script type="text/javascript" language="javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript" language="javascript" src="js/jquery.jsonview.js"></script>
<script type="text/javascript" language="javascript" src="js/vkbeautify.js"></script>
<script type="text/javascript" language="javascript" src="media/js/jquery.dataTables.js"></script>
<script type="text/javascript" language="javascript" class="init">
	$(document).ready(function() {
		$('#datatablecontainer').slideUp();
		$('.rawDataSearch').each(function(){
			var _this = $(this);
			$(this).find('#btnsubmit').click(function(){
				$('#datatablecontainer').slideUp();
				var queryurl='/eInsight/template/alert';
				var queryPath = '';
				_this.find('.eventinput').each(function(){
					var id = $(this).attr('id');
					var value = $(this).val();
					if(id != 'limit') {
						queryPath='/'+ $(this).attr('id') +'/'+ value;
					} else {
						queryPath = queryPath + '/'+ $(this).val();
					}
				});

				showDataTables(queryurl + queryPath, 'get');

				$("#datatables").load(function(){
					$('#datatablecontainer').slideDown();
				});
			});
		});

	});
	function showDataTables(url, method) {
		$('#datatables').attr('src','showDatatables?url='+encodeURI(url));
	}
	</script>
</head>
<body>
	<!--/.nav-collapse -->
	<jsp:include page="header.jsp" >
	<jsp:param name="option" value="index"/>
	</jsp:include>
	<!--/.nav-collapse -->
	<br/>
	<br/>
	<br/>
	<div class="container-fluid">
		<div class="row-fluid rawDataSearch">
			<div class="span12" style="overflow: auto;">
				<div class="input-prepend">
					<span class="add-on">AlertType=</span> 
					<select class="eventinput" id="Type" style='width: 280px;'>
				         <option value='Alert_EMAIL'>Email Alerts</option>
				         <option value='ReportAlert'>System Alerts</option>
				         <option value='TaskError'>Insight Task Alerts</option>
				         <option value='WorkerDown'>Insight Node Alerts</option>
						</select>
				</div>
				top <input type="text" value='10' style="width:30px;" class="eventinput" id="limit">
				<div class="input-prepend">
					<button type="button" class="btn btn-primary" id="btnsubmit">Query!</button>
				</div>
			</div>
		</div>
		 <div class="row-fluid" id='datatablecontainer' style=''>
			 <iframe  id='datatables' name='datatables' basic_src='showDatatables' style='width:100%;height:2000px;border:0px;'></iframe>
		 </div>
	</div>
</body>
</html>