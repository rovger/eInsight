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
<link href="media/css/jquery.dataTables.css" rel="stylesheet">
<script type="text/javascript" language="javascript" src="media/js/jquery.js"></script>
<script type="text/javascript" language="javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript" language="javascript" src="media/js/jquery.dataTables.js"></script>
<title>eInsight</title>
<script type="text/javascript" language="javascript" class="init">
$(document).ready(function() {
	 $('#demo').html( '<table cellpadding="0" cellspacing="0"  style="aligh:center;" border="0" class="display" id="example"></table>' );
		 var table = $('#example').dataTable({
			 "dom": 'T<"clear">lfrtip',
			 "tableTools": {
					"sSwfPath": "media/tabletools/swf/copy_csv_xls_pdf.swf"
				},
			 "sProcessing":"loading...",
			 "paging": false,
			 "pageLength": 7,
			 "pagingType":"simple",
			 "autoWidth": false,
			 "searching":false,
			 "data": ${model.aadata},
			 "columns": ${model.aacolumns},
			 "order":[[1,'desc']]
		});
});
	function share(){
		window.parent.window.open(window.location.href);
	}
	</script>
</head>
<body>
	<div class="container-fluid">
	<div class="row-fluid">
			<div class="span12" style="overflow: auto;">
				<div id="demo"></div>
			</div>
		</div>
	</div>
</body>
</html>