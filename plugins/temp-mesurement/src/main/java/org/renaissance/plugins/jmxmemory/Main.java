package org.renaissance.plugins.tempmesurement;


import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import org.renaissance.Plugin;

import com.profesorfalken.jsensors.*;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.sensors.Temperature;
import com.profesorfalken.jsensors.model.sensors.Fan;

public class Main implements Plugin,
    Plugin.AfterOperationSetUpListener,
    Plugin.BeforeOperationTearDownListener,
    Plugin.MeasurementResultPublisher {

  Components components;
  List<Cpu> cpus;

  HashMap<String, Long> resultsBefore = new HashMap<String, Long>();
  HashMap<String, Long> resultsAfter = new HashMap<String, Long>();

  
  public void logInConsole(){
    if (cpus != null) {
        for (final Cpu cpu : cpus) {
            System.out.println("Found CPU component: " + cpu.name);
            if (cpu.sensors != null) {
              System.out.println("Sensors: ");
  
              //Print temperatures
              List<Temperature> temps = cpu.sensors.temperatures;
              for (final Temperature temp : temps) {
                  System.out.println(temp.name + ": " + temp.value.longValue());
              }
  
              //Print fan speed
              List<Fan> fans = cpu.sensors.fans;
              for (final Fan fan : fans) {
                  System.out.println(fan.name + ": " + fan.value + " RPM");
              }
            }
        }
    }
  }


  public Main() {
    this.components = JSensors.get.components();

    this.cpus = components.cpus;
  }



  @Override
  public void afterOperationSetUp(String benchmark, int opIndex, boolean isLastOp) {
    //logInConsole();

    this.components = JSensors.get.components();

    this.cpus = components.cpus;

    resultsBefore.clear();
    resultsAfter.clear();

    if(cpus != null) {
      for (final Cpu cpu : cpus) {
          if (cpu.sensors != null) {
  
            for (final Temperature temp : cpu.sensors.temperatures) {
              resultsBefore.put("`" + cpu.name + "` Temperature in C: `" + temp.name + "`:", temp.value.longValue());
            }
  
            for (final Fan fan : cpu.sensors.fans) {
              resultsBefore.put("`" + cpu.name + "` Fan speed in RPM: `" + fan.name + "`:", fan.value.longValue());
            }
          }
      }
    
    }

  }

  @Override
  public void beforeOperationTearDown(String benchmark, int opIndex, long harnessDuration) {
    //logInConsole();

    this.components = JSensors.get.components();

    this.cpus = components.cpus;

    if (cpus != null) {
      for (final Cpu cpu : cpus) {
          if (cpu.sensors != null) {
  
            for (final Temperature temp : cpu.sensors.temperatures) {
              resultsAfter.put("`" + cpu.name + "` Temperature in C: `" + temp.name + "`:", temp.value.longValue());
            }
  
            for (final Fan fan : cpu.sensors.fans) {
              resultsAfter.put("`" + cpu.name + "` Fan speed in RPM: `" + fan.name + "`:", fan.value.longValue());
            }
          }
      }
    }
  }

  @Override
  public void onMeasurementResultsRequested(String benchmark, int opIndex, Plugin.MeasurementResultListener dispatcher) {
    ArrayList<String> keys = new ArrayList<String>(resultsBefore.keySet());
    for (String resultBefore : keys) { 
      String key = resultBefore;
      dispatcher.onMeasurementResult(benchmark, "jsensor " + key + " ... Before ... ", resultsBefore.get(key));
      if (resultsAfter.containsKey(key)) {
        dispatcher.onMeasurementResult(benchmark, "jsensor " + key + " ... After ... ", resultsAfter.get(key));
        dispatcher.onMeasurementResult(benchmark, "jsensor " + key + "... Diff before and after ... ", resultsAfter.get(key) - resultsBefore.get(key));
      }
    }
  }

  private void warn(String msg, Object... args) {
    System.err.printf("[jsensor] WARNING" + msg + "%n", args);
  }
}
