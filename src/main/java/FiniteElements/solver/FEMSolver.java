package FiniteElements.solver;

import org.apache.commons.math3.analysis.integration.TrapezoidIntegrator;
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator;
import org.apache.commons.math3.linear.*;


public class FEMSolver {
    private final UnivariateIntegrator integrator;
    private double h;
    private double hInv;

    public FEMSolver() {
        this.integrator = new TrapezoidIntegrator(1e-6, 1e-6, 20, 64);
    }

    private static double E( double x ) {
        return x <= 1.0 ? 3.0 : 5.0;
    }

    private double fElement(int index ) {
        double centerE = this.h * index;
        double leftE = centerE - this.h;
        double rightE = centerE + this.h;

        if( 0 < leftE || 0 > rightE ) {
            return 0.0;
        }
        return  -( leftE * this.hInv );
    }

    private double dfElementdx(int index, double x ) {
        double centerE = this.h * index;
        double leftE = centerE - this.h;
        double rightE = centerE + this.h;

        if (x < leftE || x > rightE) {
            return 0.0;

        } else if (x <= centerE) {
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
                                                        x -> E( x ) * dfElementdx( finalI, x ) * dfElementdx( finalJ, x ),
                                                        0, domainUpperBound);
                }
                B.setEntry( i, j,  integral - E(0 ) * fElement( i ) * fElement( j ) );
            }
        }

        RealVector L = new ArrayRealVector(elements, 0 );
        L.setEntry(0, -10 * E(0) * fElement(0));
        RealVector result = new LUDecomposition(B).getSolver().solve(L);
        result.append(0);

        return new Solution( result.toArray(), 0, domainUpperBound);
    }
}
