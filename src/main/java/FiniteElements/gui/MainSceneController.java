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
import java.util.ResourceBundle;



public class MainSceneController implements Initializable {
    @FXML
    private LineChart<Number, Number> plot;

    @FXML
    private TextField numberOfElements;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        solve(3);
    }

    private void solve( int elements ) {
        FEMSolver solver = new FEMSolver();
        Solution solution = solver.solve( elements );
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for ( int i = 0; i < solution.result().length; i++ ) {
            double x = ( solution.domainRight() - solution.domainLeft() ) * i / ( solution.result().length - 1 );
            series.getData().add( new XYChart.Data<>( x, solution.result()[ i ] ) );
        }
        if( this.plot.getData().size() > 0 ) {
            this.plot.getData().remove(0 );
        }
        this.plot.getData().add(0, series );
    }

    @FXML
    public void btnClick(){
        if(this.numberOfElements.getText().matches("\\d+" )){
            solve(Integer.parseInt(this.numberOfElements.getText()));
        } else{
            throw new IllegalArgumentException( "Number of elements must be a positive integer" );
        }
    }
}

