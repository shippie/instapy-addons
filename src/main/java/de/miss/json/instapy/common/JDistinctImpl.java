package de.miss.json.instapy.common;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JDistinctImpl implements JDistinct {
  private final JsonIO jsonIO;

  public JDistinctImpl(final JsonIO jsonIO) {
    this.jsonIO = jsonIO;
  }

  @Override
  public void distinct(final PropertyHolder propertyHolder) {
    try {
      final Path pSourceA = Paths.get(propertyHolder.inputfile);
      final Path pSourceB = Paths.get(propertyHolder.inputfile2);
      final List<JsonNode> jsonNodesA = this.jsonIO.readJson(pSourceA);
      final List<JsonNode> jsonNodesB = this.jsonIO.readJson(pSourceB);

      final List<String> jsonNodesATxt =
          jsonNodesA.stream().map(x -> x.asText()).collect(Collectors.toList());
      final List<String> jsonNodesBTxt =
          jsonNodesB.stream().map(x -> x.asText()).collect(Collectors.toList());

      jsonNodesATxt.addAll(jsonNodesBTxt);

      final List<String> distincted =
          jsonNodesATxt.stream().distinct().collect(Collectors.toList());

      for (final String objNode : distincted) {
        log.info("Distinct Node Text: {}", objNode);
      }

    } catch (final Exception e) {
      log.error("Error,{} ", e);
    }
  }
}
