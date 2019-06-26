 var myChart = echarts.init(document.getElementById('episode'));
        var pathname = window.location.pathname;
        var tbody=window.document.getElementById("tbody-result");

        var episode = [];
        var view = [];
        var title = [];
	    //AJAX接收数据主体
        $.ajax({
            type:"GET",
            url:"/api/" + pathname,
            dataType:"json",
            async:false,
            success:function (result) {
                var str = "";
                for (var i = 0; i < result.value.length; i++){
                    episode.push(result.value[i].title);
                    view.push(result.value[i].view);

                    str += "<tr>" +
                        "<td>" + result.value[i].title+
                        '<a href="/view_version?tableName=episode_detail&rowName='+result.value[i].aid+'&familyName=detail_info&qualifier=view">' +
                        " (播放曲线)" + "</a>" + "</td>" +
                        "<td>" + result.value[i].ptime+ "</td>" +
                        "<td>" + result.value[i].ups + "</td>" +
                        "<td>" + result.value[i].view+ "</td>" +
                        "</tr>";
                }
                title.push(result.title);
                tbody.innerHTML = str;
            },
            error :function(errorMsg) {
                alert("获取后台数据失败！");
            }
        });

        // 指定图表的配置项和数据
        var option = {
            title: {
                text: '每集播放量',
                subtext: '数据来自bilibili'
            },
            tooltip: {},
            legend: {
                data: title
            },

            xAxis: {
        	    //结合
                data: episode,
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
                name: title,
                type: 'bar',
                //结合
                data: view
            }]
            };

            // 使用刚指定的配置项和数据显示图表。
            myChart.setOption(option);