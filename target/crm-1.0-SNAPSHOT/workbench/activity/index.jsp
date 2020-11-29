<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String basePath = request.getScheme() + "://" +
request.getServerName() + ":" + request.getServerPort() +
request.getContextPath() + "/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">
<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>
	<link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
	<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
	<script type="text/javascript" src="jquery/bs_pagination/en.js"></script>
<script type="text/javascript">

	$(function(){
		//为addBtn添加点击事件，打开添加操作的模态窗口
		$("#addBtn").click(function (){
			//添加日期选择器
			$(".time").datetimepicker({
				minView: "month",
				language:  'zh-CN',
				format: 'yyyy-mm-dd',
				autoclose: true,
				todayBtn: true,
				pickerPosition: "bottom-left"
			});
			//通过ajax获取数据库信息，往下拉列表里添加数据
			$.ajax({
				url:"workbench/activity/getUserList.do",
				type:"get",
				dataType:"json",
				success:function (resp){
					var html = "";
					$.each(resp,function (i,n){
						html+="<option value='"+n.id+"'>"+n.name+"</option>"
					})
					$("#create-owner").html(html);
					//在下拉列表中默认选择当前用户
					var id = "${sessionScope.user.id}"
					$("#create-owner").val(id);
					//需要操作模态窗口的jQuery对象，调用modal方法，为该方法传递参数，show：展现 hide：隐藏
					$("#createActivityModal").modal("show");
				}
			})
			$("#save").click(function (){
				$.ajax({
					url:"workbench/activity/save.do",
					data:{
						"owner":$.trim($("#create-owner").val()),
						"name":$.trim($("#create-name").val()),
						"startDate":$.trim($("#create-startDate").val()),
						"endDate":$.trim($("#create-endDate").val()),
						"cost":$.trim($("#create-cost").val()),
						"description":$.trim($("#create-description").val())
					},
					type: "post",
					dataType: "json",
					success:function (resp){
						//将表单内填的数据重置，只能用dom对象中的reset属性
						$("#activityAddForm")[0].reset();
						if (resp.success){
							$("#createActivityModal").modal("hide");
							/*
							pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
									,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
							$("#activityPage").bs_pagination('getOption', 'currentPage')表示操作后停留在当前页
							$("#activityPage").bs_pagination('getOption', 'rowsPerPage')操作后维持已经设计好的每页展现记录数
							*/
							//表示做完添加操作后，应该回到第一页，并且维持修改后每页展现的记录数
							pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
						}
						else{
							alert("添加活动失败");
						}
					}
				})
			})
		})
        //页面加载完毕刷新市场活动列表
        pageList(1,2);
		//点击创建模块里的保存刷新列表
		//点击查询刷新市场活动列表
        $("#searchBtn").click(function (){
            $("#hidden-name").val($.trim($("#search-name").val()));
            $("#hidden-owner").val($.trim($("#search-owner").val()));
            $("#hidden-startDate").val($.trim($("#search-startDate").val()));
            $("#hidden-endDate").val($.trim($("#search-endDate").val()));
			pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
					,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
        })
        //为多选框添加点击事件
        $("#qx").click(function (){
            $("input[name=xz]").prop("checked",this.checked);
        })
        //为单选框添加事件，因为$("input[name=xz]")是动态生成的元素不能直接绑定普通事件
        /*语法
            $(需要绑定的元素的有效外层元素).on(绑定事件的方式，需要绑定的元素的jQuery对象，回调函数)
        */
        $("#activityBody").on("click",$("input[name=xz]"),function (){
                $("#qx").prop("checked",$("input[name=xz]").length==$("input[name=xz]:checked").length);
        })
        //为删除按钮添加点击事件
        $("#deleteBtn").click(function (){
            var $xz = $("input[name=xz]:checked");
            if ($xz.length==0){
                alert("请先选择想要删除的数据");
            }else{
                var param = "";
                for (var i=0;i<$xz.length;i++){
                    param+="id="+$xz[i].value;
                    if (i<$xz.length-1){
                        param+="&";
                    }
                }
                if (confirm("您确定要删除吗？")){
                    $.ajax({
                        url:"workbench/activity/delete.do",
                        data:param,
                        dataType:"json",
                        type:"post",
                        success:function (data){
                            if (data.success){
								pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
							}else{
                                alert("删除信息失败");
                            }
                        }
                    })
                }
            }
        })
		$("#editBtn").click(function (){
			//首先获取复选框选中的
			var $xz = $("input[name=xz]:checked");
			if ($xz.length==0){
				alert("请先选择要修改的数据");
			}else if($xz.length>1){
				alert("请选择一条要修改数据");
			}else{
				var id = $xz.val();
				$.ajax({
					url:"workbench/activity/getUserListAndActivity.do",
					data:{
						"id":id
					},
					dataType:"json",
					type:"get",
					//ajax中的data{"userList",[{用户1}，{2}],"a",[{市场活动1}]}
					success:function (data){
						var html = "";
						$.each(data.userList,function (i,n){
							html+="<option value='"+n.id+"'>"+n.name+"</option>";
						})
						$("#edit-owner").html(html);
						$("#edit-id").val(data.activity.id);
						$("#edit-name").val(data.activity.name);
						$("#edit-startDate").val(data.activity.startDate);
						$("#edit-endDate").val(data.activity.endDate);
						$("#edit-cost").val(data.activity.cost);
						$("#edit-description").val(data.activity.description);
						//加完值后打开修改的模态窗口
						$("#editActivityModal").modal("show");
					}
				})
			}
		})
		//为更新按钮添加事件，完成修改操作
		$("#updateBtn").click(function (){
			$.ajax({
				url:"workbench/activity/update.do",
				data:{
					"id":$.trim($("#edit-id").val()),
					"owner":$.trim($("#edit-owner").val()),
					"name":$.trim($("#edit-name").val()),
					"startDate":$.trim($("#edit-startDate").val()),
					"endDate":$.trim($("#edit-endDate").val()),
					"cost":$.trim($("#edit-cost").val()),
					"description":$.trim($("#edit-description").val())
				},
				type: "post",
				dataType: "json",
				success:function (resp){
					//将表单内填的数据重置，只能用dom对象中的reset属性
					if (resp.success){
						$("#editActivityModal").modal("hide");
						/*
                        pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
                                ,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
                        $("#activityPage").bs_pagination('getOption', 'currentPage')表示操作后停留在当前页
                        $("#activityPage").bs_pagination('getOption', 'rowsPerPage')操作后维持已经设计好的每页展现记录数
                        */
						//表示做完添加操作后，应该回到第一页，并且维持修改后每页展现的记录数
						pageList($("#activityPage").bs_pagination('getOption', 'currentPage'),$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
					}
					else{
						alert("修改活动失败");
					}
				}
			})
		})
	});

	function pageList(pageNo,pageSize) {
	    //每次刷新将全选的复选框默认改为未勾选
        $("#qx").prop("checked",false);
        //查询前，将隐藏域中的保存的信息取出来，重新赋予到搜索框中
	    $("#search-name").val($.trim($("#hidden-name").val()));
        $("#search-owner").val($.trim($("#hidden-owner").val()));
        $("#search-startDate").val($.trim($("#hidden-startDate").val()));
        $("#search-endDate").val($.trim($("#hidden-endDate").val()));
	    //刷新展现市场活动列表
        $.ajax({
            url:"workbench/activity/pageList.do",
            data: {
                "pageNo":pageNo,
                "pageSize":pageSize,
                "name":$.trim($("#search-name").val()),
                "owner":$.trim($("#search-owner").val()),
                "startDate":$.trim($("#search-startDate").val()),
                "endDate":$.trim($("#search-endDate").val())
            },
            dataType:"json",
            type:"get",
            success:function (data){
                /*
                后端应该提供，市场查询的列表，和总条数
                需要的json {"total":"100","activityList":[{市场活动1}，{2}，{3}]}
                拼接字符串
                <tr class="active">
							<td><input type="checkbox" /></td>
							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
							<td>2020-10-10</td>
							<td>2020-10-20</td>
				</tr>
                * */
                //循环json中的key="activityList"的value,每一个n就是一个activity对象
                var html = "";
                $.each(data.dataList,function (i,n){
                html+= '<tr class="active">';
                html+= '<td><input type="checkbox" name="xz" value="'+n.id+'"/></td>';
                html+= '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/detail.do?id='+n.id+'\';">'+n.name+'</a></td>';
                html+= '<td>'+n.owner+'</td>';
                html+= '<td>'+n.startDate+'</td>';
                html+= '<td>'+n.endDate+'</td>';
                html+= '</tr>'
                })
                //将拼接好的字符串添加到<tbody id="activityBody">中
                $("#activityBody").html(html);
                //处理完数据后，结合分页插件对前端显示分页信息
				//计算总页数
				var totalPages = data.total%pageSize == 0?data.total/pageSize:parseInt(data.total/pageSize)+1;
				$("#activityPage").bs_pagination({
					currentPage: pageNo, // 页码
					rowsPerPage: pageSize, // 每页显示的记录条数
					maxRowsPerPage: 20, // 每页最多显示的记录条数
					totalPages: totalPages, // 总页数
					totalRows: data.total, // 总记录条数

					visiblePageLinks: 3, // 显示几个卡片

					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					showRowsDefaultInfo: true,

					onChangePage : function(event, data){
						pageList(data.currentPage , data.rowsPerPage);
					}
				});
            }
        })
    }
</script>
</head>
<body>
    <%--创建隐藏域保存查询输入框的内容--%>
    <input type="hidden" id="hidden-name">
    <input type="hidden" id="hidden-owner">
    <input type="hidden" id="hidden-startDate">
    <input type="hidden" id="hidden-endDate">
	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form id="activityAddForm" class="form-horizontal" role="form">
					
						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-owner">
									<%--<option>--%>
								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-name">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-startDate">
							</div>
							<label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-endDate">
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="save">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">
						<input type="hidden" id="edit-id">
						<div class="form-group">
							<label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-owner">
								  <%--<option>zhangsan</option>
								  <option>lisi</option>
								  <option>wangwu</option>--%>
								</select>
							</div>
                            <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-name">
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-startDate">
							</div>
							<label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-endDate">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<%--关于标<textarea>虽然是以标签对出现但是没有value属性，所以依然是表单元素范畴，取值赋值用val()方法，不是html()--%>
								<textarea class="form-control" rows="3" id="edit-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="updateBtn">更新</button>
				</div>
			</div>
		</div>
	</div>
	
	
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control" type="text" id="search-startDate" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control" type="text" id="search-endDate">
				    </div>
				  </div>
				  
				  <button type="button" id="searchBtn" class="btn btn-default">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
					<%--给addBtn添加点击时间：data-toggle="modal" data-target="#createActivityModal"--%>
				  <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="qx" /></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="activityBody">
						<%--<tr class="active">
							<td><input type="checkbox" /></td>
							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
							<td>2020-10-10</td>
							<td>2020-10-20</td>
						</tr>
                        <tr class="active">
                            <td><input type="checkbox" /></td>
                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
                            <td>2020-10-10</td>
                            <td>2020-10-20</td>
                        </tr>--%>
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 30px;">
				<div id="activityPage"></div>
			</div>
			
		</div>
		
	</div>
</body>
</html>