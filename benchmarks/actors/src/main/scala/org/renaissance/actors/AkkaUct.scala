package org.renaissance.actors

import edu.rice.habanero.actors.AkkaActorState
import edu.rice.habanero.benchmarks.uct.UctAkkaActorBenchmark
import org.renaissance.Config
import org.renaissance.License
import org.renaissance.RenaissanceBenchmark

class AkkaUct extends RenaissanceBenchmark {
  override def description(): String =
    "Runs the Unbalanced Cobwebbed Tree actor workload in Akka."

  override def licenses(): Array[License] = License.create(License.MIT)

  override def defaultRepetitions = 24

  private var bench: UctAkkaActorBenchmark.UctAkkaActorBenchmark = null

  override def setUpBeforeAll(c: Config): Unit = {
    bench = new UctAkkaActorBenchmark.UctAkkaActorBenchmark
    bench.initialize(new Array[String](0))
    AkkaActorState.initialize()
  }

  override def tearDownAfterAll(c: Config): Unit = {
    if (bench != null) {
      bench.cleanupIteration(false, 0)
    }
  }

  protected override def runIteration(config: Config): Unit = {
    for (i <- 0 until 10) {
      bench.runIteration()
    }
  }
}