package repicea.math;

@SuppressWarnings("serial")
public abstract class AbstractMathematicalFunctionWrapper extends AbstractMathematicalFunction<Integer, Double, Integer, Double> {

	protected final AbstractMathematicalFunction<Integer, Double, Integer, Double> originalFunction;

	public AbstractMathematicalFunctionWrapper(AbstractMathematicalFunction<Integer, Double, Integer, Double> originalFunction) {
		this.originalFunction = originalFunction;
	}

	/**
	 * This method returns the original function.
	 * @return an AbstractMathematicalFunction instance
	 */
	public AbstractMathematicalFunction<Integer, Double, Integer, Double> getOriginalFunction() {
		return originalFunction;
	}
	
	@Override
	public abstract Double getValue();

	@Override
	public abstract Matrix getGradient();

	@Override
	public abstract Matrix getHessian();
	

	@Override
	public void setParameterValue(Integer parameterName, Double parameterValue) {
		originalFunction.setParameterValue(parameterName, parameterValue);
	}

	@Override
	public Double getParameterValue(Integer parameterName) {
		return originalFunction.getParameterValue(parameterName);
	}

	@Override
	public void setVariableValue(Integer variableName, Double variableValue) {
		originalFunction.setVariableValue(variableName, variableValue);
	}

	@Override
	public Double getVariableValue(Integer variableName) {
		return originalFunction.getVariableValue(variableName);
	}

	@Override
	public int getNumberOfParameters() {return originalFunction.getNumberOfParameters();}

	@Override
	public int getNumberOfVariables() {return originalFunction.getNumberOfVariables();}

}