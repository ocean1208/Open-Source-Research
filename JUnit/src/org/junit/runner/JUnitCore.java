package org.junit.runner;

import java.util.ArrayList;
import java.util.List;

import junit.runner.Version;
import org.junit.internal.TextListener;
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

/**
 * <code>JUnitCore</code> is a facade for running tests. It supports running JUnit 4 tests, 
 * JUnit 3.8.x tests, and mixtures. To run tests from the command line, run 
 * <code>java org.junit.runner.JUnitCore TestClass1 TestClass2 ...</code>.
 * For one-shot test runs, use the static method {@link #runClasses(Class[])}. 
 * If you want to add special listeners,
 * create an instance of {@link org.junit.runner.JUnitCore} first and use it to run the tests.
 * 
 * @see org.junit.runner.Result
 * @see org.junit.runner.notification.RunListener
 * @see org.junit.runner.Request
 */
public class JUnitCore {
	private static my.Debug DEBUG=new my.Debug(my.Debug.JUnitCore);//我加上的
	
	private RunNotifier fNotifier;

	/**
	 * Create a new <code>JUnitCore</code> to run tests.
	 */
	public JUnitCore() {
		try {//我加上的
		DEBUG.P(this,"JUnitCore()");

		fNotifier= new RunNotifier();

		}finally{//我加上的
		DEBUG.P(0,this,"JUnitCore()");
		}
	}

	/**
	 * Run the tests contained in the classes named in the <code>args</code>.
	 * If all tests run successfully, exit with a status of 0. Otherwise exit with a status of 1.
	 * Write feedback while tests are running and write
	 * stack traces for all failed tests after the tests all complete.
	 * @param args names of classes in which to find tests to run
	 */
	public static void main(String... args) {
		try {//我加上的
		DEBUG.P(JUnitCore.class,"main(1)");
		DEBUG.PA("args",args);

		Result result= new JUnitCore().runMain(args);
		killAllThreads(result);

		}finally{//我加上的
		DEBUG.P(0,JUnitCore.class,"main(1)");
		}
	}

	private static void killAllThreads(Result result) {
		System.exit(result.wasSuccessful() ? 0 : 1);
	}
	
	/**
	 * Run the tests contained in <code>classes</code>. Write feedback while the tests
	 * are running and write stack traces for all failed tests after all tests complete. This is
	 * similar to {@link #main(String[])}, but intended to be used programmatically.
	 * @param classes Classes in which to find tests
	 * @return a {@link Result} describing the details of the test run and the failed tests.
	 */
	public static Result runClasses(Class<?>... classes) {
		return new JUnitCore().run(classes);
	}
	
	/**
	 * Do not use. Testing purposes only.
	 */
	public Result runMain(String... args) {
		try {//我加上的
		DEBUG.P(this,"runMain(1)");
		DEBUG.PA("args",args);

		System.out.println("JUnit version " + Version.id());
		List<Class<?>> classes= new ArrayList<Class<?>>();
		List<Failure> missingClasses= new ArrayList<Failure>();
		for (String each : args)
			try {
				classes.add(Class.forName(each));
			} catch (ClassNotFoundException e) {
				System.out.println("Could not find class: " + each);
				Description description= Description.createSuiteDescription(each);
				Failure failure= new Failure(description, e);

				DEBUG.P("e="+e);
				DEBUG.P("e.getMessage()="+e.getMessage());
				DEBUG.P("failure="+failure);
				missingClasses.add(failure);
			}
		RunListener listener= new TextListener();
		addListener(listener);
		Result result= run(classes.toArray(new Class[0]));
		for (Failure each : missingClasses)
			result.getFailures().add(each);
		return result;

		}finally{//我加上的
		DEBUG.P(0,this,"runMain(1)");
		}
	}

	/**
	 * @return the version number of this release
	 */
	public String getVersion() {
		return Version.id();
	}
	
	/**
	 * Run all the tests in <code>classes</code>.
	 * @param classes the classes containing tests
	 * @return a {@link Result} describing the details of the test run and the failed tests.
	 */
	public Result run(Class<?>... classes) {
		try {//我加上的
		DEBUG.P(this,"run(1)");
		DEBUG.PA("classes",classes);

		return run(Request.classes("All", classes));

		}finally{//我加上的
		DEBUG.P(0,this,"run(1)");
		}
	}

	/**
	 * Run all the tests contained in <code>request</code>.
	 * @param request the request describing tests
	 * @return a {@link Result} describing the details of the test run and the failed tests.
	 */
	public Result run(Request request) {
		try {//我加上的
		DEBUG.P(this,"run(Request request)");
		DEBUG.P("request="+request);

		return run(request.getRunner());

		}finally{//我加上的
		DEBUG.P(0,this,"run(Request request)");
		}
	}

	/**
	 * Run all the tests contained in JUnit 3.8.x <code>test</code>. Here for backward compatibility.
	 * @param test the old-style test
	 * @return a {@link Result} describing the details of the test run and the failed tests.
	 */
	public Result run(junit.framework.Test test) { 
		return run(new JUnit38ClassRunner(test));
	}
	
	/**
	 * Do not use. Testing purposes only.
	 */
	public Result run(Runner runner) {
		try {//我加上的
		DEBUG.P(this,"run(Runner runner)");
		DEBUG.P("runner="+runner);

		Result result= new Result();
		RunListener listener= result.createListener();
		addFirstListener(listener);
		try {
			fNotifier.fireTestRunStarted(runner.getDescription());
			runner.run(fNotifier);
			fNotifier.fireTestRunFinished(result);
		} finally {
			removeListener(listener);
		}
		return result;

		}finally{//我加上的
		DEBUG.P(0,this,"run(Runner runner)");
		}
	}
	
	private void addFirstListener(RunListener listener) {
		fNotifier.addFirstListener(listener);
	}
	

	/**
	 * Add a listener to be notified as the tests run.
	 * @param listener the listener to add
	 * @see org.junit.runner.notification.RunListener
	 */
	public void addListener(RunListener listener) {
		fNotifier.addListener(listener);
	}

	/**
	 * Remove a listener.
	 * @param listener the listener to remove
	 */
	public void removeListener(RunListener listener) {
		fNotifier.removeListener(listener);
	}
}
