/**
 * Created by glockett on 31/03/2016.
 */

$('.collapse').on('show.bs.collapse', function () {
    $('.collapse.in').collapse('hide');
});



$(document).ready(function(){
    $("#report tr:odd").addClass("odd");
    $("#report tr:not(.odd)").hide();
    $("#report tr:first-child").show();

    $("#report tr.odd").click(function(){
        $(this).next("tr").toggle();
        $(this).find(".arrow").toggleClass("up");
    });
    //$("#report").jExpand();
});




