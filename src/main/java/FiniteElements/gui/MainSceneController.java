package FiniteElements.gui;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import FiniteElements.solver.FEMSolver;
import FiniteElements.solver.Solution;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;



public class MainSceneController implements Initializable {
    @FXML
    private LineChart<Number, Number> plot;

    @FXML
    private TextField numberOfElements;

    @FXML
    private Button solveButton;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        solve(3);
    }

    private void solve( int elements ) {
        FEMSolver solver = new FEMSolver(20, 64);
        Solution solution = solver.solve(elements);
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (int i = 0; i < solution.result().length; i++) {
            double x = (solution.domRight() - solution.domLeft()) * i / (solution.result().length - 1);
            series.getData().add(new XYChart.Data<>(x, solution.result()[i]));
        }
        if(this.plot.getData().size() > 0) {
            this.plot.getData().remove(0);
        }
        this.plot.getData().add(0, series);
    }

    @FXML
    public void btnClick(){
        solve(Integer.parseInt(numberOfElements.getText()));
    }
}

