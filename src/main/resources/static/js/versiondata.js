var myChart = echarts.init(document.getElementById('version'));
            var search = window.location.search;

            var time = [];
            var value = [];
	        //AJAX接收数据主体
            $.ajax({
                type:"GET",
                url:"/get/versiondata" + search,
                dataType:"json",
                async:false,
                success:function (result) {
                    for (var i = 0; i < result.length; i++){
                        time.push(result[i].time);
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
                    text: '播放量',
                    subtext: '数据来自bilibili'
                },
                tooltip: {},
                legend: {
                data:['view']
                },

                xAxis: {
                    type: 'category',
                    data: time,
                    name: '爬取时间'
                },

                yAxis: [{
                    type : 'value',
                    axisLabel : {
                        formatter: function(value){
                            if (value >=10000) {
        			        value = value/10000+'w';
        		        }else if(value <10000){
        			        value = value/1000+'k';
        		        }
        		        return value
                        }
                    }
                }],
                series: [{
                    name: 'view',
                    type: 'line',
                    data: value,
                }]
                };

                // 使用刚指定的配置项和数据显示图表。
                myChart.setOption(option);