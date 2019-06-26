var myChart = echarts.init(document.getElementById('main'));

            var title = [];
            var value = [];
	        //AJAX接收数据主体
            $.ajax({
                type:"GET",
                url:"/anime/favorite/10/top",
                dataType:"json",
                async:false,
                success:function (result) {
                    for (var i = 0; i < result.length; i++){
                        title.push(result[i].title);
                        value.push(result[i].value);
                    }

                },
                error :function(errorMsg) {
                alert("获取后台数据失败！");
                }
            });

            // 指定图表的配置项和数据
            var option = {
                title: {
                    text: '追番人数',
                    subtext: '数据来自bilibili'
                },
                tooltip: {},
                legend: {
                    data:['favorite']
                },

                xAxis: {
        	    //结合
                    data: title,
                    axisLabel:{
                        interval:0,
                        rotate:45
                    },
                },

                yAxis: [{
                type : 'value',
                axisLabel : {
                    formatter: function(value){
                        if (value >=10000) {
        			    value = value/10000+'w';
        		    }else if(value <1000){
        			    value = value/1000+'k';
        		    }
        		    return value
                    }
                }
                }],
                series: [{
                    name: 'favorite',
                    type: 'bar',
                    //结合
                    data: value
                }]
                };

                // 使用刚指定的配置项和数据显示图表。
                myChart.setOption(option);

                var myChart = echarts.init(document.getElementById('main2'));

                                    var title = [];
                                    var value = [];
                	                //AJAX接收数据主体
                                    $.ajax({
                                        type:"GET",
                                        url:"/detail/view/10/top",
                                        dataType:"json",
                                        async:false,
                                        success:function (result) {
                                            for (var i = result.length-1; i >= 0; i--){
                                                title.push(result[i].title);
                                                value.push(result[i].value);
                                            }

                                        },
                                         error :function(errorMsg) {
                                            alert("获取后台数据失败！");
                                        }
                                    });

                	                var option = {
                	                    color: ['#3398DB'],
                                        title: {
                                            text: '单集/合集 插放总量排行',
                                            subtext: '数据来自bilibili'
                                        },
                                        tooltip: {
                                            trigger: 'axis',
                                            axisPointer: {
                                                type: 'shadow'
                                            }
                                        },
                                        legend: {
                                            data: ['view']
                                        },

                                        xAxis: {
                                            type: 'value',
                                            axisLabel : {
                                                formatter: function(value){
                                                    if (value >=10000) {
                        			                value = value/10000+'w';
                        		                }else if(value <1000){
                        			                value = value/1000+'k';
                        		                }
                        		            return value
                                                }
                                            }
                                        },
                                        yAxis: {
                                            type: 'category',
                                            axisLabel:{
                                                interval:0,
                                                rotate:10
                                            },
                                            data: title
                                        },
                                        series: [
                                        {
                                            name: 'view',
                                            type: 'bar',
                                            data: value
                                        },
                                        ]
                                    };
                                    myChart.setOption(option);

var myChart = echarts.init(document.getElementById('main3'));
                    var score = [];
                    var value = [];

                    //AJAX接收数据主体
                    $.ajax({
                        type:"GET",
                        url:"/anime/score",
                        dataType:"json",
                        async:false,
                        success:function (result) {
                            for (var i = 0; i < result.length; i++){
                                if(i==0){
                                    score.push("无评分");
                                    value.push({name: "无评分",value: result[i].value});
                                } else if(i>0){
                                    score.push(result[i].score);
                                    value.push({name: result[i].score,value: result[i].value});
                                }
                            }
                        },
                        error :function(errorMsg) {
                            alert("获取后台数据失败！");
                        }
                    });
                    var option = {
                        tooltip: {
                            trigger: 'item',
                            formatter: "{a} <br/>{b}: {c} ({d}%)"
                        },
                        legend: {
                            orient: 'vertical',
                            x: 'left',
                            data: score
                        },
                        series: [
                        {
                            name:'评分区间',
                            type:'pie',
                            radius: ['50%', '70%'],
                            avoidLabelOverlap: false,
                            label: {
                                normal: {
                                    show: false,
                                    position: 'center'
                                },
                            emphasis: {
                                show: true,
                                textStyle: {
                                    fontSize: '30',
                                    fontWeight: 'bold'
                                }
                            }
                        },
                        labelLine: {
                            normal: {
                                show: false
                            }
                        },
                        data: value
                        }
                        ]
                        };
                        myChart.setOption(option);