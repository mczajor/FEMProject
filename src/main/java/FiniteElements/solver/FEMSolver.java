package FiniteElements.solver;

import org.apache.commons.math3.analysis.integration.TrapezoidIntegrator;
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator;
import org.apache.commons.math3.linear.*;


public class FEMSolver {
    private final UnivariateIntegrator integrator;
    private double h;
    private double hInv;

    public FEMSolver() {
        this.integrator = new TrapezoidIntegrator(1e-5, 1e-5, 20, 64);
    }

    private static double E( double x ) {
        return x <= 1.0 ? 3.0 : 5.0;
    }

    private double e_i(int index ) {
        double element = this.h * index;
        double prevElement = element - this.h;
        double nextElement = element + this.h;

        if( 0 < prevElement || 0 > nextElement ) {
            return 0.0;
        }
        return  -( prevElement * this.hInv );
    }

    private double de_idx(int index, double x ) {
        double element = this.h * index;
        double prevElement = element - this.h;
        double nextElement = element + this.h;

        if (x < prevElement || x > nextElement) {
            return 0.0;
        } else if ( x <= element ) {
            return this.hInv;
        }
        return -this.hInv;
    }

    public Solution solve( int elements ) {

        double domainUpperBound = 2.0;
        this.h = domainUpperBound / elements;
        this.hInv = 1 / this.h;

        RealMatrix B = new Array2DRowRealMatrix(elements, elements);

        for (int i = 0; i < elements; i++ ) {
            for (int j = 0; j < elements; j++ ) {
                double integral = 0;
                if ( Math.abs( i - j ) <= 1 ) {
                    int finalI = i;
                    int finalJ = j;
                    integral = this.integrator.integrate( Integer.MAX_VALUE,
                                                        x -> E( x ) * de_idx( finalI, x ) * de_idx( finalJ, x ),
                                                        0, domainUpperBound);
                }
                B.setEntry( i, j,  integral - E(0 ) * e_i( i ) * e_i( j ) );
            }
        }

        RealVector L = new ArrayRealVector(elements, 0 );
        L.setEntry(0, -10 * E(0) * e_i(0));
        RealVector result = new LUDecomposition(B).getSolver().solve(L);
        result.append(0);

        return new Solution( result.toArray(), 0, domainUpperBound);
    }
}
