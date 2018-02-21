<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- Meta, title, CSS, favicons, etc. -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title><fmt:message key="aimir.version"/> </title>

    <link href="../vendors/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="../vendors/font-awesome/css/font-awesome.min.css" rel="stylesheet">
    <link href="../vendors/nprogress/nprogress.css" rel="stylesheet">
    <link href="../vendors/iCheck/skins/flat/green.css" rel="stylesheet">
	
    <link href="../vendors/bootstrap-progressbar/css/bootstrap-progressbar-3.3.4.min.css" rel="stylesheet">
    <link href="../vendors/jqvmap/dist/jqvmap.min.css" rel="stylesheet"/>

    <link href="../build/css/custom.min.css" rel="stylesheet">
    <link href="../css/common.css" rel="stylesheet">
    <fmt:setLocale value='${lang}' scope="session"/>

  </head>

  <body class="nav-md">
    <div class="container body">
      <div class="main_container">
        <div class="col-md-3 left_col">
          <div class="left_col scroll-view">
          	
            <!-- sidebar menu -->
			<jsp:include page="left.jsp" flush="true"/>
			
          </div>
        </div>

        <!-- top navigation -->
		<jsp:include page="top.jsp" flush="true"/>
				
        <!-- page content -->
        <div class="right_col" role="main">
          <!-- top tiles -->
          <div class="row tile_count">
            <div class="col-md-2 col-sm-4 col-xs-6 tile_stats_count">
              <span class="count_top"><i class="fa fa-clock-o"></i> <fmt:message key="aimir.gateway.count"/> </span>
              <div>
              	<span class="count">0</span><span class="tile_small_text"><h3> EA</h3></span>
              </div>
            </div>
            <div class="col-md-2 col-sm-4 col-xs-6 tile_stats_count">
              <span class="count_top"><i class="fa fa-clock-o"></i> <fmt:message key="aimir.waerable.count"/> </span>
              <div>
              	<span class="count">0</span><span class="tile_small_text"><h3> EA</h3></span>
              </div>
            </div>
            <div class="col-md-2 col-sm-4 col-xs-6 tile_stats_count">
              <span class="count_top"><i class="fa fa-clock-o"></i> <fmt:message key="aimir.24.success"/> </span>
              <div>
              	<span class="count">0</span><span class="tile_small_text"><h3> %</h3></span>
              </div>
            </div>
            <div class="col-md-2 col-sm-4 col-xs-6 tile_stats_count">
              <span class="count_top"><i class="fa fa-clock-o"></i> <fmt:message key="aimir.48.success"/></span>
              <div>
              	<span class="count">0</span><span class="tile_small_text"><h3> %</h3></span>
              </div>
            </div>
            <div class="col-md-2 col-sm-4 col-xs-6 tile_stats_count">
              <span class="count_top"><i class="fa fa-clock-o"></i> <fmt:message key="aimir.avr.success"/></span>
              <div>
              	<span class="count">0</span><span class="tile_small_text"><h3> %</h3></span>
              </div>
            </div>
			<div class="col-md-2 col-sm-4 col-xs-6 tile_stats_count">
              <span class="count_top"><i class="fa fa-clock-o"></i> <fmt:message key="aimir.fail.comm"/></span>
              <div class="red">
              	<span class="count">0</span><span class="tile_small_text"><h3> EA</h3></span>
              </div>
            </div>
          </div>
          <!-- /top tiles -->
	
          <!-- aimir IoT tiles -->
          <div class="row">
            <div class="col-md-4 col-sm-6 col-xs-12 widget_tally_box">
            	<div class="x_panel tile">
	                <div class="x_title">
	                  <h2><fmt:message key="aimir.gateway.chart.title"/></h2>
	                  <ul class="nav navbar-right panel_toolbox">
	                    <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a></li>
	                    <li><a class="close-link"><i class="fa fa-close"></i></a></li>
	                  </ul>
	                  <div class="clearfix"></div>
	                </div>
	                <div class="x_content">
	                	<div style="height:200px;">
	                  		<div id="gateway_donut" style="height: 100%"></div>
	                  	</div>
	                  	<div style="height:200px;">
	                  		<div id="gateway_bar" style="height: 100%"></div>
	                  	</div>
	                </div>
            	</div>
            </div>
            <div class="col-md-4 col-sm-6 col-xs-12 widget_tally_box">
            	<div class="x_panel tile">
	                <div class="x_title">
	                  <h2><fmt:message key="aimir.wearable.chart.title"/></h2>
	                  <ul class="nav navbar-right panel_toolbox">
	                    <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a></li>
	                    <li><a class="close-link"><i class="fa fa-close"></i></a></li>
	                  </ul>
	                  <div class="clearfix"></div>
	                </div>
	                <div class="x_content">
	                	<div style="height:200px;">
	                  		<div id="wearable_donut" style="height: 100%"></div>
	                  	</div>
	                  	<div style="height:200px;">
	                  		<div id="wearable_bar" style="height: 100%"></div>
	                  	</div>
	                </div>
            	</div>
            </div>
         	<div class="col-md-4 col-sm-6 col-xs-12 widget_tally_box">
         		<div class="x_panel">
	            	<div class="x_title">
	                	<h2><fmt:message key="aimir.wearable.status.chart.title"/></h2>
	                    <ul class="nav navbar-right panel_toolbox">
	                      <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a></li>
	                      <li><a class="close-link"><i class="fa fa-close"></i></a></li>
	                    </ul>
	                    <div class="clearfix"></div>
	                </div>
	                <div class="x_content">
	                	<div style="height:200px;">
	                  		<div id="status_donut" style="height: 100%"></div>
	                  	</div>
	                  	<div style="height:200px;">
	                  		<div id="status_bar" style="height: 100%"></div>
	                  	</div>
	                </div>
         		</div>
          	</div>
          </div>
          <br />
          
         <div class="row">
         	<div class="col-md-4 col-sm-6 col-xs-12 widget_tally_box">
         		<div class="x_panel">
	                <div class="x_title">
	                  <h2><fmt:message key="aimir.hrm.chart.title"/></h2>
	                  <ul class="nav navbar-right panel_toolbox">
	                    <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a></li>
	                    <li><a class="close-link"><i class="fa fa-close"></i></a></li>
	                  </ul>
	                  <div class="clearfix"></div>
	                </div>
	                <div class="x_content">
	                	<div style="height:200px;">
	                  		<div id="hrm_donut" style="height: 100%"></div>
	                  	</div>
	                  	<div style="height:200px;">
	                  		<div id="hrm_bar" style="height: 100%"></div>
	                  	</div>
	                </div>
         		</div>
         	</div>
         </div>
          <!-- /aimir IoT tiles -->

        </div>
        <!-- /page content -->
      </div>
    </div>
    
    <script src="../vendors/jquery/dist/jquery.min.js"></script>
    <script src="../vendors/bootstrap/dist/js/bootstrap.min.js"></script>
    <script src="../vendors/bootstrap-progressbar/bootstrap-progressbar.min.js"></script>
    <script src="../vendors/moment/min/moment.min.js"></script>
    
    <script src="../vendors/echarts/dist/echarts.min.js"></script>
	<script src="../js/theme/echart.theme.js"></script>
	<script src="../js/jquery.dataTables.min.js"></script>
	
    <!-- Custom Theme Scripts -->
    <script src="../build/js/custom.min.js"></script>
	
	<script type="text/javascript">
	    var loginId = "";
	    var roleId = "";
	    
	    $.ajaxSetup({
	        async : false
	    });
	    
	    $( document ).ready(function() {
	    	init();
	    });
	    
	    function init() {
	    	getUserInfo();
	    	getIndexParameter();
	    }
	    	    
	    function getUserInfo() {
	    	$.getJSON('${ctx}/common/getUserInfo.do', function(json) {
	 	        if (json.supplierId != "") {
	 	            loginId = json.loginId;
	 	            $('#accoutId').html(loginId);
	 	        }
	 	        if ( json.roleId != ""){
	 	        	roleId = parseInt(json.roleId);
	 	        }
	 	    });
	    }
	    
	   function getIndexParameter() {
	    	$.getJSON('${ctx}/gaget/main/getMainParameter.do', function(json) {
	    		if(json.status == 'SUCCESS') {
	    			setTopGrid(json.topGrid);
	    			
	    			setGatewayDonutChart(json.gateway);
	    			setGatewayBarChart(json.gateway);
	    			
	    			setWearableDonutChart(json.wearable);
	    			setWearableBarChart(json.wearable);
	    			
	    			setStatusDonutChart(json.deviceStatus);
	    			setStatusBarChart(json.deviceStatus);
	    			
	    			setHrmDonutChart(json.hrm);
	    			setHrmBarChart(json.hrm);
	    		}
	 	    });
	   }
	   
	   function setTopGrid(content) {
		   $('.count').each(function(i) {
			  $(this).html(content[i]);
		   });
	   }
	   
	   function setHrmBarChart(content) {
		   var element = document.getElementById('hrm_bar');
		   if(element == null)
			   return;
		   
		   var echartBar = echarts.init(element, theme);
		   echartBar.setOption({
				calculable: true,
				xAxis: [{
				  type: 'value',
				  boundaryGap: [0, 0.01]
				}],
				yAxis: [{
				  type: 'category',
				  data: ['', '', '', '']
				}],
				series: [{
				  type: 'bar',
				  itemStyle: {
					  normal: {
						  label: {
							  show: true,
							  position: 'right'
						  },
						  color: function(params) {
							  var colorList = [
		                          '#26B99A','#A50025', '#FF5E00', '#34495E'
		                        ];
		                        return colorList[params.dataIndex] 
						  }
					  }
				  },
				  barWidth: 17,
				  data: [{
						value: content[0],
						name: 'Normal HRM'
					}, {
						value: content[1],
						name: 'HRM High'
					}, {
						value: content[2],
						name: 'HRM Low'
					}, {
						value: content[3],
						name: 'Unknow HRM'
					}]
				}]
			  });
	   }
	   
	   function setHrmDonutChart(content) {
		   var element = document.getElementById('hrm_donut');
		   if(element == null)
			   return;
		   
		   var echartDonut = echarts.init(element, theme);
		   echartDonut.setOption({
				tooltip: {
				  trigger: 'item',
				  formatter: "{a} <br/>{b} : {c} ({d}%)"
				},
				calculable: true,		
				legend: {
			        x: 'center',
			        y: 'bottom',
			        padding: [5, 5, 0, 5],
			        data:['Normal HRM','HRM High','HRM Low','Unknow HRM']
			    },
				toolbox: {
				  show: true,
				  feature: {
					magicType: {
					  show: true,
					  type: ['pie', 'funnel'],
					  option: {
						funnel: {
						  x: '25%',
						  width: '50%',
						  funnelAlign: 'center'
						}
					  }
					}
				  }
				},
				series: [{
				  name: 'Status',
				  type: 'pie',
				  radius: ['45%', '65%'],
				  itemStyle: {
					normal: {
					  label: {
						show: true
					  },
					  labelLine: {
						show: true
					  },
					  color: function(params) {
						  var colorList = [
							  '#26B99A','#A50025', '#FF5E00', '#34495E'
	                        ];
	                        return colorList[params.dataIndex] 
					  }
					},
					emphasis: {
					  label: {
						show: true,
						position: 'center',
						textStyle: {
						  fontSize: '14',
						  fontWeight: 'normal'
						}
					  }
					}
				  },
				  data: [{
					value: content[0],
					name: 'Normal HRM'
				  	}, {
						value: content[1],
						name: 'HRM High'
					}, {
						value: content[2],
						name: 'HRM Low'
					}, {
						value: content[3],
						name: 'Unknow HRM'
					}]
				}]
			});
	   }
	   
	   function setStatusBarChart(content) {
		   var element = document.getElementById('status_bar');
		   if(element == null)
			   return;
		   
		   var echartBar = echarts.init(element, theme);
		   echartBar.setOption({
				calculable: true,
				xAxis: [{
				  type: 'value',
				  boundaryGap: [0, 0.01]
				}],
				yAxis: [{
				  type: 'category',
				  data: ['', '', '', '']
				}],
				series: [{
				  type: 'bar',
				  itemStyle: {
					  normal: {
						  label: {
							  show: true,
							  position: 'right'
						  },
						  color: function(params) {
							  var colorList = [
		                          '#26B99A','#34495E','#BDC3C7','#A50025'
		                        ];
		                        return colorList[params.dataIndex] 
						  }
					  }
				  },
				  data: [{
						value: content[0],
						name: 'Normal'
					  }, {
						value: content[1],
						name: 'LowBattery'
					  }, {
						value: content[2],
						name: 'Desorption'
					  }, {
						value: content[3],
						name: 'SOS'
					  },{
						value: content[4],
						name: 'Charge'
					  }]
				}]
			  });
	   }
	   
	   function setStatusDonutChart(content) {
		   var element = document.getElementById('status_donut');
		   if(element == null)
			   return;
		   
		   var echartDonut = echarts.init(element, theme);
		   echartDonut.setOption({
				tooltip: {
				  trigger: 'item',
				  formatter: "{a} <br/>{b} : {c} ({d}%)"
				},
				calculable: true,		
				legend: {
			        x: 'center',
			        y: 'bottom',
			        padding: [5, 5, 0, 5],
			        data:['Normal','LowBattery','Desorption','SOS','Charge']
			    },
				toolbox: {
				  show: true,
				  feature: {
					magicType: {
					  show: true,
					  type: ['pie', 'funnel'],
					  option: {
						funnel: {
						  x: '25%',
						  width: '50%',
						  funnelAlign: 'center'
						}
					  }
					}
				  }
				},
				series: [{
				  name: 'Status',
				  type: 'pie',
				  radius: ['45%', '65%'],
				  itemStyle: {
					normal: {
					  label: {
						show: true
					  },
					  labelLine: {
						show: true
					  }
					},
					emphasis: {
					  label: {
						show: true,
						position: 'center',
						textStyle: {
						  fontSize: '14',
						  fontWeight: 'normal'
						}
					  }
					}
				  },
				  data: [{
					value: content[0],
					name: 'Normal'
				  }, {
					value: content[1],
					name: 'LowBattery'
				  }, {
					value: content[2],
					name: 'Desorption'
				  }, {
					value: content[3],
					name: 'SOS'
				  },{
					value: content[4],
					name: 'Charge'
				  }]
				}]
			});
	   }
	   
	   function setWearableBarChart(content) {
		   var element = document.getElementById('wearable_bar');
		   if(element == null)
			   return;
		   
		   var echartBar = echarts.init(element, theme);
		   echartBar.setOption({
				calculable: true,
				xAxis: [{
				  type: 'value',
				  boundaryGap: [0, 0.01]
				}],
				yAxis: [{
				  type: 'category',
				  data: ['', '', '', '']
				}],
				series: [{
				  type: 'bar',
				  itemStyle: {
					  normal: {
						  label: {
							  show: true,
							  position: 'right'
						  },
						  color: function(params) {
							  var colorList = [
		                          '#26B99A','#34495E','#BDC3C7','#A50025'
		                        ];
		                        return colorList[params.dataIndex] 
						  }
					  }
				  },
				  data: [{
						value: content[0],
						name: '24Hour'
					  }, {
						value: content[1],
						name: '48Hour'
					  }, {
						value: content[2],
						name: '72Hour'
					  }, {
						value: content[3],
						name: 'Unknow'
				  }]
				}]
			  });
	   }
	   
	   function setWearableDonutChart(content) {
		   var element = document.getElementById('wearable_donut');
		   if(element == null)
			   return;
		   
   		var echartDonut = echarts.init(element, theme);
		echartDonut.setOption({
			tooltip: {
			  trigger: 'item',
			  formatter: "{a} <br/>{b} : {c} ({d}%)"
			},
			calculable: true,
			legend: {
				x: 'center',
				y: 'bottom',
				padding: [5, 5, 0, 5],
				data: ['24Hour', '48Hour', '72Hour', 'Unknow']
			},
			toolbox: {
			  show: true,
			  feature: {
				magicType: {
				  show: true,
				  type: ['pie', 'funnel'],
				  option: {
					funnel: {
					  x: '25%',
					  width: '50%',
					  funnelAlign: 'center'
					}
				  }
				}
			  }
			},
			series: [{
			  name: 'Access to the resource',
			  type: 'pie',
			  radius: ['45%', '65%'],
			  itemStyle: {
				normal: {
				  label: {
					show: true
				  },
				  labelLine: {
					show: true
				  }
				},
				emphasis: {
				  label: {
					show: true,
					position: 'center',
					textStyle: {
					  fontSize: '14',
					  fontWeight: 'normal'
					}
				  }
				}
			  },
			  data: [{
				value: content[0],
				name: '24Hour'
			  }, {
				value: content[1],
				name: '48Hour'
			  }, {
				value: content[2],
				name: '72Hour'
			  }, {
				value: content[3],
				name: 'Unknow'
			  }]
			}]
		});
	   }
	   
	   function setGatewayBarChart(content) {
		   var element = document.getElementById('gateway_bar');
		   if(element == null)
			   return;
		   
		   var echartBar = echarts.init(element, theme);
			echartBar.setOption({
				calculable: true,
				xAxis: [{
				  type: 'value',
				  boundaryGap: [0, 0.01]
				}],
				yAxis: [{
				  type: 'category',
				  data: ['', '', '', '']
				}],
				series: [{
				  type: 'bar',
				  itemStyle: {
					  normal: {
						  label: {
							  show: true,
							  position: 'right'
						  },
						  color: function(params) {
							  var colorList = [
		                          '#26B99A','#34495E','#BDC3C7','#A50025'
		                        ];
		                        return colorList[params.dataIndex] 
						  }
					  }
				  },
				  data: [{
						value: content[0],
						name: '24Hour'
					  }, {
						value: content[1],
						name: '48Hour'
					  }, {
						value: content[2],
						name: '72Hour'
					  }, {
						value: content[3],
						name: 'Unknow'
				  }]
				}]
			  });
	   }
	   
	   function setGatewayDonutChart(content) {
		   var element = document.getElementById('gateway_donut');
		   if(element == null)
			   return;
		   
		   var echartDonut = echarts.init(element, theme);
		   echartDonut.setOption({
				tooltip: {
				  trigger: 'item',
				  formatter: "{a} <br/>{b} : {c} ({d}%)"
				},
				calculable: true,
				legend: {
					x: 'center',
					y: 'bottom',
					padding: [5, 5, 0, 5],
					data: ['24Hour', '48Hour', '72Hour', 'Unknow']
				},
				toolbox: {
				  show: true,
				  feature: {
					magicType: {
					  show: true,
					  type: ['pie', 'funnel'],
					  option: {
						funnel: {
						  x: '25%',
						  width: '50%',
						  funnelAlign: 'center'
						}
					  }
					}
				  }
				},
				series: [{
				  name: 'Access to the resource',
				  type: 'pie',
				  radius: ['45%', '65%'],
				  itemStyle: {
					normal: {
					  label: {
						show: true
					  },
					  labelLine: {
						show: true
					  }
					},
					emphasis: {
					  label: {
						show: true,
						position: 'center',
						textStyle: {
						  fontSize: '14',
						  fontWeight: 'normal'
						}
					  }
					}
				  },
				  data: [{
					value: content[0],
					name: '24Hour'
				  }, {
					value: content[1],
					name: '48Hour'
				  }, {
					value: content[2],
					name: '72Hour'
				  }, {
					value: content[3],
					name: 'Unknow'
				  }]
				}]
			});
	   }
	   
	   
	  
	</script>
  </body>
</html>
