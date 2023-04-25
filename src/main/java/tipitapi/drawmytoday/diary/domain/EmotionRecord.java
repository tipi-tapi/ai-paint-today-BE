package tipitapi.drawmytoday.diary.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.emotion.domain.Emotion;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "emotion_record")
public class EmotionRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long recordId;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "diary_id", nullable = false)
  private Diary diary;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "emotion_id", nullable = false)
  private Emotion emotion;

  @NotNull
  private int seq;

  public EmotionRecord(Diary diary, Emotion emotion, int seq) {
    this.diary = diary;
    this.emotion = emotion;
    this.seq = seq;
  }

  public static EmotionRecord create(Diary diary, Emotion emotion, int seq) {
    return new EmotionRecord(diary, emotion, seq);
  }
}
