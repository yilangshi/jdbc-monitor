var baseUrl = "";
//中间内容页固定大小
window.on
//加载首页
$.get('statistics_summary.html', function (data) {
  $('#home_content').html(data);
});
//添加菜单事件
$('.sidebar-wrapper .nav li a').on('click',function () {
  //添加样式
  $('.sidebar-wrapper .nav li').removeClass('active');
  $(this).parent('li').attr('class','active');
  //设置title
  $('#main_title').html($(this).find('p').html());
  //加载页面
  var value = $(this).attr('value');
  if(value != ''){
    $.get(value, function (data) {
      $('#home_content').html(data);
    });
  }else{
    $('#home_content').html('');
  }
});

