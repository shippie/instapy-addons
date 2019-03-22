package de.miss.json.instapy.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "json")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PropertyHolder {

  String inputfile;
  String inputfile2;

  Integer splitsize;
}
