<%@ page trimDirectiveWhitespaces="true"
	contentType="text/html; charset=UTF-8"%>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
		<%
		   String uri = request.getRequestURI();
		   out.println(uri);
		   String index = "inactive";
		   String reporting = "inactive";
		   String alert = "inactive";
		   String admin = "inactive";
		   if(uri.contains("rawDataSearch")) index = "active";
		   else if(uri.contains("reporting")) reporting = "active";
		   else if(uri.contains("alert")) alert = "active";
		   else if(uri.contains("admin")) admin = "active";
		   else index="active";
	%>  
<div class="navbar  navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container-fluid">
				<li class="brand dropdown">
	                   ${model.domain} EasyInsight
	             </li>
				<div class="nav-collapse collapse">
					<ul class="nav">
						<li class=<%out.print(reporting); %> ><a href="reporting">Insight Reporting</a></li>
					</ul>
				</div>
				<div class="nav-collapse collapse pull-right">
					<ul class="nav">
						<li  class="inactive"><a href="alert"><strong>Alert</strong></a></li>
					</ul>
				</div>
			</div>
		</div>
	</div>