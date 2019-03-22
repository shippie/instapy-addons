package de.miss.json.instapy.common;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class JsonIO {

  public List<JsonNode> readJson(final Path pSource) {
    try {
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
      log.info("--- Read finished: {} ---", pSource.getFileName());
      return ImmutableList.copyOf(actualObj.iterator());
    } catch (final IOException e) {
      log.error("Reading error {}", e);
      throw new RuntimeException(e);
    }
  }

  protected Path createOutputFile(final Path sourceFileName, final String infix) {
    log.debug(
        "Filename created from sourcefilename {} and infix {}", sourceFileName.getParent(), infix);
    final String sourceFileNameStr = sourceFileName.getFileName().toString();
    final String sourceFileNameBasicStr = StringUtils.substringBeforeLast(sourceFileNameStr, ".");
    final String sourceFileNameExtStr = StringUtils.substringAfterLast(sourceFileNameStr, ".");
    final String destFileNameStr =
        MessageFormat.format(
            "{0}_{1}.{2}",
            sourceFileNameBasicStr, StringUtils.trimToEmpty(infix), sourceFileNameExtStr);
    log.debug("Create Filename {}", destFileNameStr);
    if (sourceFileName.getParent() == null) return Paths.get(".", destFileNameStr);
    else return sourceFileName.getParent().resolve(destFileNameStr);
  }

  protected void writeToOutputfile(final Path destinationFileName, final Collection<String> data) {
    final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    try {
      final String jsonStr = objectWriter.withDefaultPrettyPrinter().writeValueAsString(data);
      log.info("{}", jsonStr);
      Files.writeString(
          destinationFileName,
          jsonStr,
          StandardCharsets.US_ASCII,
          StandardOpenOption.TRUNCATE_EXISTING,
          StandardOpenOption.CREATE);
    } catch (final Exception e) {
      log.error("Error writing output data {}", e);
      new RuntimeException(e);
    }
  }
}
