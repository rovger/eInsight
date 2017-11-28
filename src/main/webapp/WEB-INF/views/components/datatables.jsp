<%@ page trimDirectiveWhitespaces="true"
	contentType="text/html; charset=UTF-8"%>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>   
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>eInsight</title>
<meta name="viewport" content="initial-scale=1.0, maximum-scale=2.0">
<!-- Bootstrap -->
<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="css/DT_bootstrap.css">
<link rel="stylesheet" type="text/css" href="css/jquery.qtip.min.css">
<link rel="stylesheet" type="text/css" href="media/css/jquery.dataTables.css">
<link rel="stylesheet" type="text/css" href="css/jquery.jsonview.css">
<script type="text/javascript" language="javascript" src="media/js/jquery.js"></script>
<script type="text/javascript" language="javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript" language="javascript" src="media/js/jquery.dataTables.js"></script>
<script type="text/javascript" language="javascript" src="js/jquery.jsonview.js"></script>
<script type="text/javascript" language="javascript" src="js/vkbeautify.js"></script>
<script type="text/javascript" language="javascript" class="init">
$(document).ready(function() {
	showDataTables();
});

Array.prototype.contains = function(obj) {
    var i = this.length;
    while (i--) {
        if (this[i] === obj) {
            return true;
        }
    }
    return false;
}
function showDataTables(){
	 $('#demo').html( '<table cellpadding="0" cellspacing="0" border="0" class="display" id="example"></table>' );
	 $.ajax(
		{
			type : '${model.method}',
			url : '${model.url}',
			data : '${model.data}',
			contentType: 'application/json; charset=UTF-8',
			async:false,
			dataType: 'json',
			success : function(json)
			{
				var aaData=[];
				var aaColumns=[];
				var aaColumnNames=[];
				//try{json = $.parseJSON(json);}catch(Exception){}
				if(json.error){
					$('#notfound').show();
			    	$('#notfound').html(json.error);
				}
				try{$('#totalcount').html(json.summary.count);}catch(Exception){$('#waiting').hide();$('#notfound').show();}
				 $.each(json.detail,function(index,jsonobj){
					 $.each(jsonobj,function(key,value){
						 var apObj = {"title":key,"class": "center" };
						 if(aaColumnNames.contains(key)==false){
							 aaColumnNames.push(key);
							 if(key == '_id'){
								 return;
							 }
							 aaColumns.push(apObj);
						 }
					 });
				 });
				 var count = 0;
				 $.each(json.detail,function(index,jsonobj){
					 var aData=[];
					 $.each(aaColumnNames,function(key,colname){
						 var value=getFromJson(jsonobj,colname);
						 if(colname == '_id'){
							 return;
						 }
						 
						 else if(typeof value == 'object' && value != null){
								var jsontext = vkbeautify.jsonmin(JSON.stringify(value));
							    aData.push('<div class = "jsonview" id = "jsonview' + count + '" style="width:100%">' + jsontext + '</div>');
							    count++;
							   
						 } else if(typeof value == 'string' && value.length>150){
							 var jsontext = vkbeautify.jsonmin(value);
							 aData.push('<div class = "jsonview" id = "jsonview' + count + '" style="width:100%">' + jsontext + '</div>');
							 count++;
						 }
						 else{
							 if(value==null){
								 value="null";
							 }
							 aData.push('<div style="text-align:center;word-wrap:break-word;">'+value+'</div>');
						 }
					 });
					 aaData.push(aData);
				 });
				 var table;
				 if(aaData.length>0 && aaColumns.length>0){
					table = $('#example').dataTable( {
						 "sDom": '<"top"fli>rt<"bottom"p><"clear">',
						 "tableTools": {
					            "sSwfPath": "media/tabletools/swf/copy_csv_xls_pdf.swf"
					        },
					     "oLanguage": {
						     "sEmptyTable": "No results found",
						 	 "sZeroRecords": "No results found",
						 	 "sInfo": 'Query Conditions:  ${model.url} <br/><br/>Total of _TOTAL_ entries (_START_ to _END_) to <a href="javascript:share();">Share</a> ',
				         	 "sProcessing": "Searching...",
				         	 "sProcessing": "DataTables is currently busy",
				         	 "sZeroRecords": "No records to display"
					     },
						 "paging": false,
						 "data": aaData,
						 "columns": aaColumns,
						 "order":[[2,'desc']]
						 
				    } );
				 }else {
					 table = $('#example').dataTable( {
						 "sDom": '<"top"fli>rt<"bottom"p><"clear">',
						 "tableTools": {
					            "sSwfPath": "media/tabletools/swf/copy_csv_xls_pdf.swf"
					        },
						 'bSort': false,
						 "paging": false,
				         'aoColumns': [ { sWidth: "45%" }, { sWidth: "45%" }, { sWidth: "10%", bSearchable: false, bSortable: false } ],
					     "oLanguage": {
						     "sEmptyTable": "No results found",
						 	 "sZeroRecords": "No results found",
				         	 "sProcessing": "Searching...",
				         	 "sInfoEmpty": "Query Conditions:  ${model.url} <br/><br/>No entries to show",
				         	 "sProcessing": "DataTables is currently busy",
				         	 "sZeroRecords": "No records to display"
					     },
				    } ); 
				 }
					 
					
					 $('#demo').show();
					$('.jsonview').each(function(){
							if(this.innerHTML != null) {
								var tmp = this.innerHTML;
								this.innerHTML = "";
								$(this).JSONView(tmp,{ collapsed: true, nl2br: true, recursive_collapser: true });
							}
						});
				 /* }
				 else {
					 $('#notfound').show();
				 } */
			},complete: function (XMLHttpRequest, textStatus) {
			},
			 error: function (xhr, ajaxOptions, thrownError) {
			    	$('#notfound').show();
			    	$('#notfound').html("DB Issue, Please try it again!");
			    }
	});
	 
}
function getFromJson(json,searchkey){
	var result='';
	$.each(json,function(key,value){
		if(key==searchkey){
			result= value;
		}
	});
	return result;
}

function share(){
	window.parent.window.open(window.location.href);
}

	</script>
</head>

<body>
	<div class="container-fluid">
	<div class="row-fluid">
					    <div id='notfound' style='text-align: center; height: 60px; font-size: 26px; padding-top: 30px;display:none;'>Nothing found...</div>
		<div id="demo">
		</div>
		</div>
	</div>
</body>
</html>