## Generating native-image configuration

The `native-image` reflection configuration file [reflect-config.json][] is generated by attaching `native-image-agent` to the JVM process that runs typical `hof` workload.

To regenerate reflection configuration:

1. Build `hof` application:

    ```shell
    sbt stage
    ```

1. Run typical `hof` workload with attached agent:

    ```shell
    target/universal/stage/bin/hof -J-agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image monotonic ~/.local/share/fish/fish_history
    ```

1. Regenerate native image with updated configuration:

    ```shell
    sbt graalvm-native-image:packageBin
    ```

[reflect-config.json]: /src/main/resources/META-INF/native-image/reflect-config.json
