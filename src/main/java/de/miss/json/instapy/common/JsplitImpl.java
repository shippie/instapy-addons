package de.miss.json.instapy.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class JsplitImpl implements JSplit {
  private final JsonIO jsonIO;

  public JsplitImpl(final JsonIO jsonIO) {
    this.jsonIO = jsonIO;
  }

  public void split(final PropertyHolder propertyHolder) {

    try {
      final Path pSource = Paths.get(propertyHolder.inputfile);
      final List<JsonNode> jsonNodes = this.jsonIO.readJson(pSource);

      final List<List<JsonNode>> splittedLists = split(jsonNodes, propertyHolder.getSplitsize());
      final AtomicInteger i = new AtomicInteger();
      splittedLists.forEach(
          listOfNode ->
              createChunks(
                  listOfNode,
                  this.jsonIO.createOutputFile(pSource, String.valueOf(i.getAndIncrement()))));
    } catch (final Exception e) {
      log.error("Error,{} ", e);
    }
  }

  protected static List<List<JsonNode>> split(final List<JsonNode> jsonNodeList, final int size) {
    return Lists.partition(jsonNodeList, size);
  }

  private static void createChunks(final List<JsonNode> jsonNodes, final Path pOutput) {

    final ObjectMapper mapper = new ObjectMapper();
    final ArrayNode arrayNode = mapper.createArrayNode();

    jsonNodes.forEach(node -> arrayNode.add(node));

    try {
      log.info(
          "Generated JSON output\n{}",
          mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode));
      log.info("Write chunk data to {}", pOutput.toFile().getAbsolutePath());
      Files.writeString(
          pOutput,
          mapper.writeValueAsString(arrayNode),
          StandardCharsets.US_ASCII,
          StandardOpenOption.TRUNCATE_EXISTING,
          StandardOpenOption.CREATE);
    } catch (final IOException e) {
      log.error("Error writing output data {}", e);
      new RuntimeException(e);
    }
  }
}
