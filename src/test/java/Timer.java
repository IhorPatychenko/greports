public class Timer {

    private Long startTime;
    private boolean startCalled = false;
    private Long stopTime;
    private boolean stopCalled = false;

    public static Timer newInstance(){
        return new Timer();
    }

    private Timer(){}

    public Timer start() throws Exception {
        if(!startCalled){
            startTime = System.currentTimeMillis();
            startCalled = true;
        } else {
            throw new Exception("start method invoked for more than once");
        }
        return this;
    }

    public Timer stop() throws Exception {
        if(!startCalled){
            throw new Exception("stop method cannot be invoked before start method has been invoked");
        }
        if(!stopCalled){
            stopTime = System.currentTimeMillis();
            stopCalled = true;
        } else {
            throw new Exception("stop method invoked for more than once");
        }
        return this;
    }

    public Timer reset() {
        startTime = null;
        stopTime = null;
        startCalled = false;
        stopCalled = false;
        return this;
    }

    public long getTime() {
        return stopTime - startTime;
    }

    public void printTime() {
        System.out.printf("%s: %d ms%n", this.toString(), this.getTime());
    }
}
