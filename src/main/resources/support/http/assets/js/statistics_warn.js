//加载概要信息统计
$.ajax({
  url:'statistics_warn.json',
  dataType:'json',
  success:function (data) {
    if(data){
      var html = '';
      for(var i=0;i<data.length;i++){
        html += '<div class="typo-line">\n' +
          '          <h4 style="margin:30px 0 15px 80px;"><p class="category">'+data[i].name+'</p>'+getValueStr(data[i].value)+'</h4>\n' +
          '      </div>\n';
      }
      $('#statistics_warn_content').html(html);
    }
  }
});

function getValueStr(value){
  if(typeof(value) == 'undefined' || value === ''){
    return '&nbsp;';
  }
  if(Array.isArray(value)){
    var str = '';
    for(var i = 0;i < value.length; i++){
      if(value[i] == null || value[i] == ''
        || typeof(value[i]) == 'undefined')
        continue;
      if(str != '') str += ',';
      str += value[i];
    }
    return str == ''?'&nbsp;':str;
  }else{
    return value;
  }
}