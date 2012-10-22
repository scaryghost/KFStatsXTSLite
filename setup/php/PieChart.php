<html>
  <head>
    <?php
        require_once 'Common.php';
        Common::generatePieChart("DeathsData.php", 1368, 768, "chart_div", "Deaths");
    ?>
  </head>

  <body>
    <!--Div that will hold the pie chart-->
    <div id="chart_div" style="width: 1368px; margin-left: auto; margin-right: auto;"></div>
  </body>
</html>

