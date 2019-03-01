package de.miss.json.split;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class Jsplit implements CommandLineRunner {

  @Value("${inputfile}")
  private String inputfile;

  @Value("${splitsize}")
  private int splitsize;

  @Override
  public void run(final String... args) throws Exception {

    final Path pSource = Paths.get(inputfile);
    final String jsonData = Files.readString(pSource);

    final ObjectMapper mapper = new ObjectMapper();
    final JsonFactory factory = mapper.getFactory();
    final JsonParser parser = factory.createParser(jsonData);
    final JsonNode actualObj = mapper.readTree(parser);
    Assert.notNull(actualObj, "read is null");

    //    actualObj.iterator()
    for (final JsonNode objNode : actualObj) {
      log.info("Read Node Value: {}", objNode.asText());
    }

    final List<JsonNode> jsonNodes = ImmutableList.copyOf(actualObj.iterator());

    final List<List<JsonNode>> splittedLists = split(jsonNodes, splitsize);
    final AtomicInteger i = new AtomicInteger();
    splittedLists.forEach(
        listOfNode ->
            createChunks(
                listOfNode, createOutputFile(pSource, String.valueOf(i.getAndIncrement()))));
  }

  protected List<List<JsonNode>> split(final List<JsonNode> jsonNodeList, final int size) {
    return Lists.partition(jsonNodeList, size);
  }

  private void createChunks(final List<JsonNode> jsonNodes, final Path pOutput) {

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
          StandardOpenOption.TRUNCATE_EXISTING);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  private Path createOutputFile(final Path sourceFileName, final String infix) {
    log.info(
        "Filename created from sourcefilename {} and infix {}", sourceFileName.getParent(), infix);
    final String sourceFileNameStr = sourceFileName.getFileName().toString();
    final String sourceFileNameBasicStr = StringUtils.substringBeforeLast(sourceFileNameStr, ".");
    final String sourceFileNameExtStr = StringUtils.substringAfterLast(sourceFileNameStr, ".");
    final String destFileNameStr =
        MessageFormat.format(
            "{0}_{1}.{2}",
            sourceFileNameBasicStr, StringUtils.trimToEmpty(infix), sourceFileNameExtStr);
    log.info("Create Filename {}", destFileNameStr);
    if (sourceFileName.getParent() == null) return Paths.get(".", destFileNameStr);
    else return sourceFileName.getParent().resolve(destFileNameStr);
  }
}
