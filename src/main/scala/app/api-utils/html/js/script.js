/**
 * Created by glockett on 31/03/2016.
 */

$(document).ready(function(){

    $("#report tr:odd").addClass("odd");
    $("#report tr:not(.odd)").hide();
    $("#report tr:first-child").show();

    $("#report tr.odd").click(function(){
        $(this).next("tr").toggle();
        $(this).find(".arrow").toggleClass("up");
    });
    //$("#report").jExpand();

    $(".data tr:odd").show();
    $(".data tr:even").show();

});




