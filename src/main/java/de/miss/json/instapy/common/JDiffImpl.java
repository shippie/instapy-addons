package de.miss.json.instapy.common;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JDiffImpl implements JDiff {

  private final JsonIO jsonIO;

  public JDiffImpl(final JsonIO jsonIO) {
    this.jsonIO = jsonIO;
  }

  @Override
  public void diff(final PropertyHolder propertyHolder) {
    try {
      final Path pSourceA = Paths.get(propertyHolder.inputfile);
      final Path pSourceB = Paths.get(propertyHolder.inputfile2);
      final List<JsonNode> jsonNodesA = this.jsonIO.readJson(pSourceA);
      final List<JsonNode> jsonNodesB = this.jsonIO.readJson(pSourceB);

      final List<String> jsonNodesATxt =
          jsonNodesA.stream().map(x -> x.asText()).collect(Collectors.toList());
      final List<String> jsonNodesBTxt =
          jsonNodesB.stream().map(x -> x.asText()).collect(Collectors.toList());

      jsonNodesBTxt.removeAll(jsonNodesATxt);

      for (final String objNode : jsonNodesBTxt) {
        log.info("Difference Node Text: {}", objNode);
      }
      final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
      final Path fout =
          Paths.get(
              MessageFormat.format(
                  "{0}~diff~{1}.json", simpleDateFormat.format(new Date()), jsonNodesBTxt.size()));
      this.jsonIO.writeToOutputfile(this.jsonIO.createOutputFile(fout, "0"), jsonNodesBTxt);

    } catch (final Exception e) {
      log.error("Error,{} ", e);
    }
  }
}
