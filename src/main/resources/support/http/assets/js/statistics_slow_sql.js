//加载概要信息统计
$.ajax({
  url:'statistics_slow_sql.json',
  dataType:'json',
  success:function (data) {
    if(data){
      var html = '';
      for(var i=0;i<data.length;i++){
        var st = data[i];
        html += '<tr>\n' +
          '         <td>'+getValueStr(st.SQL_DETAIL)+'</td>\n' +
          '         <td>'+getValueStr(st.SQL_EXECUTE_COUNT)+'</td>\n' +
          '         <td>'+getValueStr(st.SQL_EXECUTE_TRANSACTION_COUNT)+'</td>\n' +
          '         <td>'+getValueStr(st.SQL_EXECUTE_FAIL_COUNT)+'</td>\n' +
          '         <td>'+getValueStr(st.SQL_EXECUTE_AVG_TIME)+'</td>\n' +
          '         <td>'+getValueStr(st.SQL_EXECUTE_MAX_TIME)+'</td>\n' +
          '         <td>'+getValueStr(st.SQL_EXECUTE_TIME_RANGE)+'</td>\n' +
          '         <td>'+getValueStr(st.SQL_READ_COUNT)+'</td>\n' +
          '         <td>'+getValueStr(st.SQL_UPDATE_COUNT)+'</td>\n' +
          '         <td>'+getValueStr(st.SQL_CONCURRENCY_COUNT)+'</td>\n' +
          '         <td>'+getValueStr(st.SQL_FAILURE_INFO)+'</td>\n' +
          '      </tr>';
      }
      $('#statistics_slow_sql_content').html(html);
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