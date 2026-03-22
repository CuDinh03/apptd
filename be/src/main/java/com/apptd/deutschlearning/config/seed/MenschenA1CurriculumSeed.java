package com.apptd.deutschlearning.config.seed;

import com.apptd.deutschlearning.entity.enums.ExerciseType;
import com.apptd.deutschlearning.entity.enums.LessonCategory;
import com.apptd.deutschlearning.entity.enums.LessonLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Outline A1 theo kiểu giáo trình Menschen (24 Lektionen) — nội dung tự biên, không sao chép sách.
 */
public final class MenschenA1CurriculumSeed {

  private MenschenA1CurriculumSeed() {}

  public record VocabSpec(
      String word,
      String article,
      String pluralForm,
      String meaningVi,
      String pronunciationIpa,
      String usageNoteVi
  ) {}

  public record ExerciseSpec(ExerciseType type, String questionText, List<String> choices, String correctAnswer) {}

  public record ExamplePhraseSpec(String german, String vietnamese) {}

  public record LessonSpec(
      int menschenOrder,
      String title,
      LessonCategory category,
      List<VocabSpec> vocabs,
      List<String> usageNotesVi,
      List<ExamplePhraseSpec> examplePhrases,
      String speakingPrompt,
      List<ExerciseSpec> exercises
  ) {}

  private static ExamplePhraseSpec ph(String german, String vietnamese) {
    return new ExamplePhraseSpec(german, vietnamese);
  }

  /** Danh từ (có thể có mạo từ + số nhiều). */
  private static VocabSpec n(String word, String article, String plural, String meaningVi, String ipa, String usage) {
    return new VocabSpec(word, article, plural, meaningVi, ipa, usage);
  }

  /** Động từ / trạng từ / cụm không cần mạo từ. */
  private static VocabSpec w(String word, String meaningVi, String ipa, String usage) {
    return new VocabSpec(word, null, null, meaningVi, ipa, usage);
  }

  private static ExerciseSpec mcq(String q, String correct, String... opts) {
    return new ExerciseSpec(ExerciseType.MCQ, q, List.of(opts), correct);
  }

  private static ExerciseSpec st(String q, String correct) {
    return new ExerciseSpec(ExerciseType.SHORT_TEXT, q, List.of(), correct);
  }

  /**
   * Tìm spec curriculum theo bài trong DB (khi cột JSON trống — vẫn trả nội dung cho API).
   */
  public static Optional<LessonSpec> findSpecForLesson(LessonLevel level, Integer menschenOrder, String title) {
    if (level != LessonLevel.A1) {
      return Optional.empty();
    }
    for (LessonSpec spec : allLessons()) {
      if (menschenOrder != null && spec.menschenOrder() == menschenOrder) {
        return Optional.of(spec);
      }
    }
    if (title != null && !title.isBlank()) {
      for (LessonSpec spec : allLessons()) {
        if (title.equals(spec.title())) {
          return Optional.of(spec);
        }
      }
    }
    return Optional.empty();
  }

  /** 24 bài A1 theo thứ tự menschenOrder 1..24 */
  public static List<LessonSpec> allLessons() {
    List<LessonSpec> out = new ArrayList<>();

    out.add(new LessonSpec(1, "Begrüßungen — chào hỏi (A1)", LessonCategory.COMMUNICATION,
        List.of(
            w("Hallo", "Xin chào (thân mật)", "[ˈhalo]", "Dùng thân mật, cửa hàng nhỏ, điện thoại: „Hallo, wie geht’s?“"),
            w("Guten Tag", "Chào buổi ngày / Xin chào", "[ˈɡuːtn̩ ˈtaːk]", "Lịch sự ban ngày với người lạ/đồng nghiệp. Có thể rút: „Guten Tag!“"),
            n("Grüße", "die", "die Grüße", "Lời chào (danh từ)", "[ˈɡʀyːsə]", "„Viele Grüße!“ gửi lời thăm; „Schöne Grüße“ khi viết thư/email.")),
        List.of(
            "Chào theo thời điểm: Guten Morgen (sáng), Guten Tag (ngày), Guten Abend (tối), Gute Nacht (đi ngủ).",
            "„Hallo“ thân mật; „Guten Tag“ trung tính khi gặp người lạ hoặc trong công sở.",
            "Kết hợp: chào + câu hỏi ngắn (Wie geht’s?) để mở hội thoại."
        ),
        List.of(
            ph("Hallo! Wie geht’s?", "Chào! Khỏe không? (dùng Hallo)"),
            ph("Guten Tag, ich bin neu hier.", "Xin chào (Guten Tag), tôi mới đến đây."),
            ph("Guten Tag und willkommen!", "Xin chào (Guten Tag) và chào mừng!"),
            ph("Viele Grüße aus München!", "Thân gửi / nhiều lời chào từ München! (danh từ Grüße)"),
            ph("Schöne Grüße an deinen Vater.", "Gửi lời chào đến bố bạn. (Grüße + an + Ai)")
        ),
        "Stellen Sie sich vor: Sie treffen eine Person am Morgen. Sagen Sie höflich Hallo und stellen Sie eine kurze Frage.",
        List.of(
            mcq("Was sagt man normalerweise am Morgen?", "Guten Morgen", "Guten Morgen", "Guten Tag", "Gute Nacht"),
            st("Wie heißt „xin chào“ rất ngắn trên tiếng Đức? (1 từ)", "Hallo")
        )));

    out.add(new LessonSpec(2, "Sich vorstellen — giới thiệu bản thân (A1)", LessonCategory.COMMUNICATION,
        List.of(
            w("heißen", "được gọi là", "[ˈhaɪ̯sn̩]", "„Wie heißen Sie?“ — „Ich heiße Minh.“ (tên riêng ở cuối câu)."),
            n("Name", "der", "die Namen", "tên", "[ˈnaːmə]", "„Mein Name ist …“ / „Ich heiße …“ — mạo từ der."),
            w("kommen", "đến từ", "[ˈkɔmən]", "„Woher kommen Sie?“ — „Ich komme aus Vietnam.“ (aus + nước).")),
        List.of(
            "Hỏi tên: Wie heißen Sie? (trang trọng) / Wie heißt du? (thân mật).",
            "Trả lời tên: Ich heiße … hoặc Mein Name ist …",
            "Hỏi quốc tịch/xuất xứ: Woher kommen Sie? — Trả lời: Ich komme aus + tên nước."
        ),
        List.of(
            ph("Wie heißen Sie, bitte?", "Xin hỏi bạn tên là gì? (heißen)"),
            ph("Ich heiße Minh.", "Tôi tên là Minh. (heißen)"),
            ph("Mein Name ist Anna Schmidt.", "Tên tôi là Anna Schmidt. (der Name)"),
            ph("Woher kommen Sie?", "Bạn đến từ đâu? (kommen — hỏi)"),
            ph("Ich komme aus Vietnam und heiße Lan.", "Tôi đến từ Việt Nam và tên là Lan. (kommen + heißen)")
        ),
        "Stellen Sie sich vor: Nennen Sie Ihren Namen und sagen Sie, aus welchem Land Sie kommen.",
        List.of(
            mcq("Wie fragt man nach dem Namen?", "Wie heißen Sie?", "Wie heißen Sie?", "Wie geht es?", "Woher kommen Sie?"),
            st("Schreiben Sie den Satzanfang, wenn Sie Ihren Namen sagen. (2 Wörter)", "Ich heiße"),
            mcq("Wie beginnt man: „Ich komme aus Vietnam“?", "Ich komme aus", "Ich komme aus", "Ich danke", "Ich bin")
        )));

    out.add(new LessonSpec(3, "Zahlen & Uhrzeit — con số và giờ (A1)", LessonCategory.GRAMMAR,
        List.of(
            w("eins", "một", "[aɪ̯ns]", "Đếm: eins, zwei…; giờ: „Es ist ein Uhr“ (dùng ein), khác „eins“ khi đếm."),
            w("zwei", "hai", "[t͡svaɪ̯]", "Số 2; „um zwei Uhr“ lúc 2 giờ."),
            w("drei", "ba", "[dʀaɪ̯]", "Số 3; hay gặp trong giờ: „drei Uhr“.")),
        List.of(
            "Đếm 1–12: eins, zwei, drei… — dùng cho giờ và số đếm đơn giản.",
            "Nói giờ: Es ist + số + Uhr. „Es ist ein Uhr“ (1 giờ) dùng ein chứ không phải eins.",
            "„Um zwei Uhr“ = vào lúc 2 giờ (hẹn giờ)."
        ),
        List.of(
            ph("Es ist ein Uhr.", "Bây giờ là 1 giờ. (ein Uhr — khác eins khi đếm)"),
            ph("Es ist zwei Uhr.", "Bây giờ là 2 giờ. (zwei)"),
            ph("Es ist drei Uhr.", "Bây giờ là 3 giờ. (drei)"),
            ph("Ich zähle: eins, zwei, drei.", "Tôi đếm: một, hai, ba. (eins, zwei, drei)"),
            ph("Der Bus kommt um zwei Uhr.", "Xe buýt đến lúc 2 giờ. (zwei + Uhr)")
        ),
        "Sagen Sie: eine Zahl und die Uhrzeit. Beispiel: „Es ist drei Uhr.“",
        List.of(
            mcq("Wie heißt die Zahl 1 auf Deutsch?", "eins", "eins", "ein", "einer"),
            st("Schreibe: „3“ auf Deutsch. (1 Wort)", "drei"),
            mcq("Welche Option bedeutet „2“?", "zwei", "zwei", "zweiundzwanzig", "zwanzig")
        )));

    out.add(new LessonSpec(4, "Familie — gia đình (A1)", LessonCategory.VOCABULARY,
        List.of(
            n("Mutter", "die", "die Mütter", "mẹ", "[ˈmʊtɐ]", "„Meine Mutter heißt …“ — luôn die Mutter."),
            n("Vater", "der", "die Väter", "bố", "[ˈfaːtɐ]", "„Mein Vater ist …“ — der Vater."),
            n("Freund", "der", "die Freunde", "bạn (nam)", "[fʀʊnt]", "Bạn trai/bạn thân nam; bạn nữ: die Freundin.")),
        List.of(
            "Gia đình: meine Mutter / mein Vater — tính từ sở hữu theo giống die/der.",
            "„Die Eltern“ = bố mẹ (số nhiều).",
            "Hỏi: Wer ist das? — Das ist meine Mutter."
        ),
        List.of(
            ph("Das ist meine Mutter.", "Đó là mẹ tôi. (die Mutter)"),
            ph("Mein Vater arbeitet viel.", "Bố tôi làm việc nhiều. (der Vater)"),
            ph("Meine Mutter und mein Vater sind nett.", "Mẹ và bố tôi rất tốt. (Mutter + Vater)"),
            ph("Er ist mein Freund.", "Anh ấy là bạn (nam) của tôi. (der Freund)"),
            ph("Mein Freund heißt Tom.", "Bạn (nam) của tôi tên Tom. (Freund)")
        ),
        "Sagen Sie: Wer ist Ihre Mutter und wer ist Ihr Vater? (kurz)",
        List.of(
            mcq("Wie heißt „mẹ“ auf Deutsch?", "Mutter", "Vater", "Mutter", "Kind"),
            st("Wie heißt „bạn“ (nam) auf Deutsch? (ein Wort)", "Freund"),
            mcq("Was bedeutet „die Eltern“?", "die Eltern", "die Eltern", "der Tisch", "das Brot")
        )));

    out.add(new LessonSpec(5, "Essen & Trinken — ăn uống (A1)", LessonCategory.VOCABULARY,
        List.of(
            n("Wasser", "das", null, "nước", "[ˈvasɐ]", "„Ein Wasser, bitte.“ Trung tính das Wasser."),
            n("Kaffee", "der", null, "cà phê", "[ˈkafeː]", "„Einen Kaffee, bitte.“ — der Kaffee."),
            n("Apfel", "der", "die Äpfel", "táo", "[ˈapfl̩]", "Quả táo; số nhiều Äpfel (thêm Umlaut).")),
        List.of(
            "Gọi đồ uống: Ich möchte … / Ein …, bitte. (Ein Kaffee, bitte.)",
            "„Möchten Sie …?“ — lịch sự hỏi người khác có muốn không.",
            "Trước bữa ăn có thể nói Guten Appetit!"
        ),
        List.of(
            ph("Ich trinke gern Wasser.", "Tôi thích uống nước. (das Wasser)"),
            ph("Ein Kaffee, bitte.", "Một cà phê, làm ơn. (der Kaffee)"),
            ph("Möchten Sie Kaffee oder Wasser?", "Bạn muốn cà phê hay nước? (Kaffee + Wasser)"),
            ph("Ich esse jeden Tag einen Apfel.", "Tôi mỗi ngày ăn một quả táo. (Apfel)"),
            ph("Apfel und Wasser, das schmeckt gut.", "Táo và nước, rất ngon. (Apfel + Wasser)")
        ),
        "Was möchten Sie? Sagen Sie: Wasser oder Kaffee. (kurz)",
        List.of(
            mcq("Wie heißt „nước“ auf Deutsch?", "Wasser", "Wasser", "Saft", "Milch"),
            st("Wie heißt „táo“ auf Deutsch? (ein Wort)", "Apfel"),
            mcq("Welche Option ist „cà phê“?", "Kaffee", "Kaffee", "Tees", "Brot")
        )));

    out.add(new LessonSpec(6, "Routinen — thói quen hằng ngày (A1)", LessonCategory.COMMUNICATION,
        List.of(
            w("morgens", "buổi sáng", "[ˈmɔʁɡns]", "Trạng từ thời gian: „Ich stehe morgens um sieben auf.“"),
            w("abends", "buổi tối", "[ˈaːbn̩ts]", "„Abends sehe ich fern.“ — không cần mạo từ."),
            w("aufstehen", "thức dậy", "[ˈaʊ̯fˌʃteːən]", "Tách: stehen + auf; „Ich stehe früh auf.“ (Perfekt: bin … aufgestanden).")),
        List.of(
            "Trạng từ thời gian: morgens, mittags, abends, nachts — thường đứng đầu câu hoặc sau chủ ngữ.",
            "„Um sieben Uhr“ + Verb: Ich stehe um sieben Uhr auf.",
            "Thứ tự tách động từ: … aufstehen → Ich stehe … auf."
        ),
        List.of(
            ph("Ich stehe morgens um sieben auf.", "Tôi buổi sáng thức dậy lúc 7 giờ. (morgens + aufstehen)"),
            ph("Morgens trinke ich Tee, abends Wasser.", "Sáng tôi uống trà, tối uống nước. (morgens, abends)"),
            ph("Abends bin ich zu Hause.", "Buổi tối tôi ở nhà. (abends)"),
            ph("Ich muss jeden Tag früh aufstehen.", "Tôi phải mỗi ngày dậy sớm. (aufstehen)"),
            ph("Stehst du schon auf?", "Bạn đã dậy chưa? (aufstehen — câu hỏi)")
        ),
        "Sagen Sie: Wann stehen Sie auf? Beispiel: „Ich stehe morgens auf.“",
        List.of(
            mcq("„buổi sáng“ auf Deutsch heißt...", "morgens", "morgens", "mittags", "abends"),
            st("Schreiben Sie das Verb: thức dậy (Infinitiv, ein Wort)", "aufstehen"),
            mcq("Welche Option ist „buổi tối“?", "abends", "abends", "morgens", "nachts")
        )));

    out.add(new LessonSpec(7, "Freizeit & Hobbys — thời gian rảnh (A1)", LessonCategory.VOCABULARY,
        List.of(
            n("Hobby", "das", "die Hobbys", "sở thích", "[ˈhɔbi]", "„Mein Hobby ist Lesen.“ — số nhiều Hobbys."),
            n("Sport", "der", "die Sportarten", "thể thao", "[ʃpɔʁt]", "„Ich treibe gern Sport.“ / „Fußball spielen“."),
            w("lesen", "đọc", "[ˈleːzn̩]", "„Ich lese ein Buch.“ — động từ bất quy tắc (ich lese, du liest).")),
        List.of(
            "„In der Freizeit“ = trong thời gian rảnh; gern + Verb = thích làm gì.",
            "Hobby là danh từ trung tính: Mein Hobby ist …",
            "„Sport treiben“ = chơi thể thao / tập luyện."
        ),
        List.of(
            ph("Mein Hobby ist Lesen.", "Sở thích của tôi là đọc sách. (das Hobby + lesen)"),
            ph("Ich lese jeden Abend ein Buch.", "Tôi mỗi tối đọc một cuốn sách. (lesen)"),
            ph("Ich treibe gern Sport.", "Tôi thích chơi thể thao. (Sport)"),
            ph("Lesen ist auch ein Hobby.", "Đọc sách cũng là một sở thích. (lesen + Hobby)"),
            ph("In der Freizeit lese ich oder treibe Sport.", "Lúc rảnh tôi đọc sách hoặc chơi thể thao. (lesen + Sport)")
        ),
        "Erzählen Sie kurz: Was machen Sie gern in der Freizeit?",
        List.of(
            mcq("Was passt zu Freizeit?", "Hobby", "Hobby", "Steuer", "Arbeit"),
            st("Wie sagt man „đọc“ als Verb? (ein Wort)", "lesen"),
            mcq("Welches Wort ist oft mit Bewegung verbunden?", "Sport", "Sport", "Tisch", "Fenster")
        )));

    out.add(new LessonSpec(8, "Woche & Monate — tuần và tháng (A1)", LessonCategory.GRAMMAR,
        List.of(
            w("Montag", "thứ Hai", "[ˈmoːnˌtaːk]", "„Am Montag“ vào thứ Hai; tên thứ viết hoa."),
            n("Woche", "die", "die Wochen", "tuần", "[ˈvɔxə]", "„Diese Woche“ tuần này; „pro Woche“ mỗi tuần."),
            w("Januar", "tháng Một", "[jaˈnuːaːɐ̯]", "Tháng 1; „im Januar“ vào tháng một (im + tháng).")),
        List.of(
            "Thứ trong tuần + am: am Montag, am Sonntag.",
            "Tháng + im: im Januar, im Mai.",
            "„Heute ist …“ + thứ; „Welcher Tag ist heute?“"
        ),
        List.of(
            ph("Heute ist Montag.", "Hôm nay là thứ Hai. (Montag)"),
            ph("Am Montag habe ich Deutschunterricht.", "Thứ Hai tôi có giờ tiếng Đức. (Montag)"),
            ph("Diese Woche ist kurz.", "Tuần này ngắn. (die Woche)"),
            ph("Im Januar ist es kalt.", "Trong tháng Giêng trời lạnh. (Januar)"),
            ph("Die erste Woche im Januar beginnt gut.", "Tuần đầu tiên trong tháng Một bắt đầu tốt. (Woche + Januar)")
        ),
        "Sagen Sie: Welcher Wochentag ist heute? (kurz)",
        List.of(
            mcq("Wie heißt der erste Werktag oft?", "Montag", "Montag", "Sonntag", "Mittwoch"),
            st("Schreiben Sie: „tuần“ (ein Wort, Nomen)", "Woche"),
            mcq("Welcher Monat beginnt das Jahr?", "Januar", "Januar", "Juli", "März")
        )));

    out.add(new LessonSpec(9, "Wohnung — nhà ở (A1)", LessonCategory.VOCABULARY,
        List.of(
            n("Wohnung", "die", "die Wohnungen", "căn hộ", "[ˈvoːnʊŋ]", "Căn hộ cho thuê; „Ich wohne in einer Wohnung.“"),
            n("Zimmer", "das", "die Zimmer", "phòng", "[ˈt͡sɪmɐ]", "„Mein Zimmer ist klein.“ — phòng (trong nhà)."),
            n("Küche", "die", "die Küchen", "bếp", "[ˈkʏçə]", "„Wir kochen in der Küche.“")),
        List.of(
            "„Wohnen“ + in + Dativ: Ich wohne in einer Wohnung.",
            "Hỏi số phòng: Wie viele Zimmer hat …?",
            "„Das Zimmer“ có thể là phòng ngủ hoặc phòng chung tùy ngữ cảnh."
        ),
        List.of(
            ph("Ich wohne in einer kleinen Wohnung.", "Tôi sống trong một căn hộ nhỏ."),
            ph("Unsere Wohnung hat drei Zimmer.", "Căn hộ của chúng tôi có ba phòng."),
            ph("Die Küche ist groß.", "Nhà bếp rộng."),
            ph("Wo ist dein Zimmer?", "Phòng của bạn ở đâu?")
        ),
        "Beschreiben Sie kurz: Wie viele Zimmer hat Ihre Wohnung?",
        List.of(
            mcq("Wo kocht man?", "Küche", "Küche", "Bad", "Garage"),
            st("Wie heißt „căn hộ“? (ein Wort, Nomen)", "Wohnung"),
            mcq("Was bedeutet „Zimmer“?", "Zimmer", "Zimmer", "Straße", "Preis")
        )));

    out.add(new LessonSpec(10, "Möbel — đồ nội thất (A1)", LessonCategory.VOCABULARY,
        List.of(
            n("Tisch", "der", "die Tische", "bàn", "[tɪʃ]", "„Am Tisch“ ở bàn ăn; der Tisch."),
            n("Stuhl", "der", "die Stühle", "ghế", "[ʃtuːl]", "„Ich sitze auf dem Stuhl.“"),
            n("Bett", "das", "die Betten", "giường", "[bɛt]", "„Ich schlafe im Bett.“ — das Bett.")),
        List.of(
            "„Auf dem Stuhl sitzen“, „am Tisch essen“ — giới từ theo thói quen cố định.",
            "Der Tisch / der Stuhl / das Bett — nhớ mạo từ khi dùng với Dativ (auf dem …).",
            "Miêu tả: In meinem Zimmer gibt es …"
        ),
        List.of(
            ph("In meinem Zimmer gibt es einen Tisch.", "Trong phòng tôi có một cái bàn."),
            ph("Der Stuhl steht neben dem Bett.", "Cái ghế đặt cạnh giường."),
            ph("Ich esse am Tisch.", "Tôi ăn ở bàn."),
            ph("Das Bett ist bequem.", "Giường êm.")
        ),
        "Sagen Sie: Was steht in Ihrem Zimmer? (z.B. Tisch, Stuhl)",
        List.of(
            mcq("Wo sitzt man?", "Stuhl", "Stuhl", "Fenster", "Tür"),
            st("Wie heißt „bàn“? (ein Wort)", "Tisch"),
            mcq("Wo schläft man?", "Bett", "Bett", "Kühlschrank", "Regen")
        )));

    out.add(new LessonSpec(11, "Stadt & Weg — thành phố và đường (A1)", LessonCategory.COMMUNICATION,
        List.of(
            n("Stadt", "die", "die Städte", "thành phố", "[ʃtat]", "„Ich lebe in einer großen Stadt.“"),
            n("Straße", "die", "die Straßen", "đường phố", "[ˈʃtʀaːsə]", "„Die Bank ist in dieser Straße.“"),
            w("links", "bên trái", "[lɪŋks]", "Chỉ đường: „Die erste links.“ — đối lập: rechts.")),
        List.of(
            "Hỏi đường: Wo ist …? / Wie komme ich zur …?",
            "„In der Stadt“ — die Stadt (thành phố); Straße = đường phố.",
            "Chỉ hướng: geradeaus, links, rechts, die erste/zweite Straße."
        ),
        List.of(
            ph("Ich lebe in einer kleinen Stadt.", "Tôi sống ở một thành phố nhỏ. (die Stadt)"),
            ph("Die Bank ist in dieser Straße.", "Ngân hàng ở trên phố này. (die Straße)"),
            ph("Gehen Sie die erste Straße links.", "Đi phố đầu tiên bên trái. (Straße + links)"),
            ph("Links ist die Post, rechts die Bank.", "Bên trái là bưu điện, bên phải là ngân hàng. (links)"),
            ph("Diese Stadt hat lange Straßen.", "Thành phố này có những con phố dài. (Stadt + Straße)"),
            ph("In welcher Straße wohnen Sie?", "Bạn sống ở phố nào? (Straße)")
        ),
        "Fragen Sie höflich nach dem Weg: Wo ist die Bank?",
        List.of(
            mcq("Was ist größer: Dorf oder Stadt?", "Stadt", "Stadt", "Haus", "Zimmer"),
            st("Wie heißt „đường phố“? (ein Wort)", "Straße"),
            mcq("Gegenüber von rechts ist...", "links", "links", "oben", "hinten")
        )));

    out.add(new LessonSpec(12, "Verkehrsmittel — phương tiện (A1)", LessonCategory.VOCABULARY,
        List.of(
            n("Bus", "der", "die Busse", "xe buýt", "[bʊs]", "„Mit dem Bus fahren.“ — der Bus, Haltestelle."),
            n("Zug", "der", "die Züge", "tàu hỏa", "[t͡suːk]", "„Der Zug fährt um acht Uhr.“"),
            n("Fahrrad", "das", "die Fahrräder", "xe đạp", "[ˈfaːɐ̯ˌʀaːt]", "„Ich fahre Fahrrad.“ (không mạo từ trong cụm cố định).")),
        List.of(
            "Đi phương tiện: mit dem Bus / mit dem Zug / mit dem Auto (Dativ).",
            "„Zur Schule fahren“ = đi đến trường; „mit dem Fahrrad“ = bằng xe đạp.",
            "„Ich fahre Fahrrad“ — cụm cố định không cần mạo từ trước Fahrrad."
        ),
        List.of(
            ph("Ich fahre mit dem Bus in die Stadt.", "Tôi đi xe buýt vào trung tâm. (der Bus)"),
            ph("Der Zug kommt pünktlich.", "Tàu đến đúng giờ. (der Zug)"),
            ph("Mit dem Zug fahre ich nach Berlin.", "Tôi đi tàu đến Berlin. (Zug)"),
            ph("Ich fahre jeden Tag Fahrrad.", "Tôi mỗi ngày đi xe đạp. (Fahrrad)"),
            ph("Mein Fahrrad ist neu, der Bus ist alt.", "Xe đạp của tôi mới, xe buýt cũ. (Fahrrad + Bus)")
        ),
        "Sagen Sie: Wie fahren Sie zur Arbeit oder zur Schule?",
        List.of(
            mcq("Was fährt auf Schienen?", "Zug", "Zug", "Bus", "Schiff"),
            st("Wie heißt „xe đạp“? (ein Wort)", "Fahrrad"),
            mcq("Welches Fahrzeug hat oft eine Haltestelle?", "Bus", "Bus", "Flugzeug", "U-Bahn")
        )));

    out.add(new LessonSpec(13, "Einkaufen & Preis — mua sắm (A1)", LessonCategory.COMMUNICATION,
        List.of(
            w("kaufen", "mua", "[ˈkaʊ̯fn̩]", "„Was kostet das? Ich kaufe Brot.“ — Akk.: etwas kaufen."),
            w("teuer", "đắt", "[ˈtɔʏ̯ɐ]", "Tính từ: „Das ist zu teuer.“ — so sánh: teurer."),
            w("billig", "rẻ", "[ˈbɪlɪç]", "„Ein billiges Angebot“ — đối lập: teuer.")),
        List.of(
            "Hỏi giá: Was kostet das? / Wie viel kostet …?",
            "„Das ist zu teuer.“ — mô tả giá bằng tính từ teuer/billig.",
            "„Ich kaufe … im Supermarkt.“ — mua ở đâu."
        ),
        List.of(
            ph("Ich kaufe Brot und Milch.", "Tôi mua bánh mì và sữa. (kaufen)"),
            ph("Das ist zu teuer für mich.", "Cái đó đắt quá đối với tôi. (teuer)"),
            ph("Hier kann man billig einkaufen.", "Ở đây có thể mua rẻ. (billig + kaufen)"),
            ph("Kaufen Sie das oder nicht?", "Bạn có mua cái đó không? (kaufen)"),
            ph("Teuer oder billig — was nehmen Sie?", "Đắt hay rẻ — bạn chọn gì? (teuer, billig)")
        ),
        "Rollenspiel: Sie fragen nach dem Preis. (kurz)",
        List.of(
            mcq("Was ist das Gegenteil von teuer?", "billig", "billig", "groß", "schnell"),
            st("Wie sagt man „mua“ als Verb? (Infinitiv)", "kaufen"),
            mcq("Wenn etwas viel Geld kostet, ist es...", "teuer", "teuer", "leise", "kalt")
        )));

    out.add(new LessonSpec(14, "Kleidung — quần áo (A1)", LessonCategory.VOCABULARY,
        List.of(
            n("Hose", "die", "die Hosen", "quần", "[ˈhoːzə]", "„Ich trage eine Hose.“ — die Hose (quần dài)."),
            n("Hemd", "das", "die Hemden", "áo sơ mi", "[hɛmt]", "Áo sơ mi nam thường das Hemd; áo nữ: die Bluse."),
            n("Jacke", "die", "die Jacken", "áo khoác", "[ˈjakə]", "„Im Winter brauche ich eine Jacke.“")),
        List.of(
            "„Tragen“ = mặc/đội (quần áo, mũ). „Ich trage eine Hose.“",
            "Hỏi trang phục: Was trägst du heute?",
            "Mùa đông: warme Jacke, Mütze, Schal."
        ),
        List.of(
            ph("Ich trage eine blaue Hose.", "Tôi mặc một chiếc quần màu xanh dương. (die Hose)"),
            ph("Das Hemd ist sauber.", "Áo sơ mi sạch. (das Hemd)"),
            ph("Meine Jacke hängt im Schrank.", "Áo khoác của tôi treo trong tủ. (die Jacke)"),
            ph("Hose, Hemd und Jacke — fertig!", "Quần, áo sơ mi và áo khoác — xong! (cả 3 từ vựng)"),
            ph("Die Hose passt, aber das Hemd nicht.", "Quần vừa nhưng áo sơ mi thì không. (Hose, Hemd)")
        ),
        "Sagen Sie: Was tragen Sie heute? (kurz)",
        List.of(
            mcq("Was trägt man an den Beinen?", "Hose", "Hose", "Mütze", "Schal"),
            st("Wie heißt „áo sơ mi“ (Neutral)?", "Hemd"),
            mcq("Was ist warm im Winter oft außen?", "Jacke", "Jacke", "Socken", "Shorts")
        )));

    out.add(new LessonSpec(15, "Farben — màu sắc (A1)", LessonCategory.VOCABULARY,
        List.of(
            w("rot", "đỏ", "[ʀoːt]", "„Ein rotes Auto“ — tính từ chia theo giống/số danh từ sau."),
            w("blau", "xanh dương", "[blaʊ̯]", "„Der Himmel ist blau.“"),
            w("grün", "xanh lá", "[ɡʀyːn]", "„Das Gras ist grün.“")),
        List.of(
            "Màu sắc làm tính từ: chia theo giống/số danh từ (ein rotes Auto, die rote Tür).",
            "Hỏi màu: Welche Farbe …? / In welcher Farbe?",
            "„Lieblingsfarbe“ = màu yêu thích."
        ),
        List.of(
            ph("Die Rose ist rot.", "Hoa hồng màu đỏ. (rot)"),
            ph("Der Himmel ist blau.", "Bầu trời màu xanh dương. (blau)"),
            ph("Das Gras ist grün.", "Cỏ màu xanh lá. (grün)"),
            ph("Rot, blau und grün — ich mag alle drei.", "Đỏ, xanh dương và xanh lá — tôi thích cả ba. (rot, blau, grün)"),
            ph("Der Ball ist rot, der See blau, der Wald grün.", "Quả bóng đỏ, hồ xanh dương, rừng xanh lá. (rot, blau, grün)")
        ),
        "Nennen Sie drei Farben, die Sie mögen.",
        List.of(
            mcq("Welche Farbe hat oft der Himmel?", "blau", "blau", "rot", "gelb"),
            st("Wie heißt „đỏ“?", "rot"),
            mcq("Welche Farbe assoziiert man mit Gras?", "grün", "grün", "grau", "lila")
        )));

    out.add(new LessonSpec(16, "Wetter — thời tiết (A1)", LessonCategory.VOCABULARY,
        List.of(
            n("Sonne", "die", "die Sonnen", "mặt trời / nắng", "[ˈzɔnə]", "„Die Sonne scheint.“ / „Es ist sonnig.“"),
            n("Regen", "der", null, "mưa", "[ˈʀeːɡn̩]", "„Es regnet.“ trời mưa; „im Regen“ dưới mưa."),
            w("kalt", "lạnh", "[kalt]", "„Es ist kalt.“ — so sánh: kälter, am kältesten.")),
        List.of(
            "Hỏi thời tiết: Wie ist das Wetter? / Wie wird das Wetter?",
            "„Es regnet.“ — động từ mưa; „Die Sonne scheint.“ — nắng.",
            "Tính từ: kalt, warm, heiß, windig, bewölkt (A1+)."
        ),
        List.of(
            ph("Heute ist es sehr kalt.", "Hôm nay trời rất lạnh. (kalt)"),
            ph("Es regnet den ganzen Tag.", "Mưa cả ngày. (Regen — es regnet)"),
            ph("Die Sonne scheint hell.", "Mặt trời chiếu sáng. (die Sonne)"),
            ph("Kalt, Regen, Sonne — alles kann sein.", "Lạnh, mưa, nắng — đều có thể xảy ra. (kalt + Regen + Sonne)"),
            ph("Nach dem Regen kommt die Sonne.", "Sau cơn mưa trời lại nắng. (Regen, Sonne)")
        ),
        "Sagen Sie: Wie ist das Wetter heute?",
        List.of(
            mcq("Was fällt vom Himmel bei schlechtem Wetter?", "Regen", "Regen", "Schnee", "Wind"),
            st("Wie sagt man „lạnh“?", "kalt"),
            mcq("Was scheint oft am Sommertag?", "Sonne", "Sonne", "Nebel", "Gewitter")
        )));

    out.add(new LessonSpec(17, "Körper — cơ thể (A1)", LessonCategory.VOCABULARY,
        List.of(
            n("Kopf", "der", "die Köpfe", "đầu", "[kɔpf]", "„Mir tut der Kopf weh.“ — der Kopf."),
            n("Hand", "die", "die Hände", "tay", "[hant]", "Số nhiều Hände (Umlaut); „mit der Hand“."),
            n("Auge", "das", "die Augen", "mắt", "[ˈaʊ̯ɡə]", "„Ich habe blaue Augen.“ — Pl. Augen.")),
        List.of(
            "Bộ phận cơ thể + mạo từ: der Kopf, die Hand, das Auge.",
            "„Mir tut … weh.“ — … đau (Dativ + weh tun).",
            "Số nhiều bất quy tắc: die Hände, die Augen, die Füße."
        ),
        List.of(
            ph("Mein Kopf tut weh.", "Đầu tôi đau. (der Kopf)"),
            ph("Ich wasche mir die Hände.", "Tôi rửa tay. (die Hand → Hände)"),
            ph("Sie hat große Augen.", "Cô ấy có đôi mắt to. (das Auge)"),
            ph("Mit der Hand zeige ich auf den Kopf.", "Tôi chỉ tay lên đầu. (Hand + Kopf)"),
            ph("Ein Auge lacht, ein Auge weint.", "Một mắt cười, một mắt khóc. (Auge — ví von)")
        ),
        "Zeigen Sie: Wo ist Ihr Kopf? Wo sind Ihre Hände?",
        List.of(
            mcq("Mit was sieht man?", "Auge", "Auge", "Knie", "Fuß"),
            st("Wie heißt „đầu“?", "Kopf"),
            mcq("Was benutzt man zum Schreiben oft?", "Hand", "Hand", "Ohr", "Nase")
        )));

    out.add(new LessonSpec(18, "Gesundheit — sức khỏe (A1)", LessonCategory.COMMUNICATION,
        List.of(
            n("Arzt", "der", "die Ärzte", "bác sĩ", "[aːɐ̯tst]", "„Zum Arzt gehen.“ — bác sĩ nữ: die Ärztin."),
            n("Schmerzen", "die", null, "đau", "[ˈʃmɛʁt͡sn̩]", "Luôn số nhiều: „Ich habe Kopfschmerzen.“"),
            n("Tablette", "die", "die Tabletten", "viên thuốc", "[taˈblɛtə]", "„Eine Tablette einnehmen.“")),
        List.of(
            "„Zum Arzt gehen“ = đi khám bác sĩ; „krank sein“ = ốm.",
            "„Ich habe Schmerzen.“ — đau (luôn số nhiều Schmerzen).",
            "„Tabletten nehmen“ = uống thuốc viên."
        ),
        List.of(
            ph("Ich gehe zum Arzt.", "Tôi đi khám bác sĩ. (der Arzt)"),
            ph("Ich habe starke Schmerzen.", "Tôi đau dữ dội. (die Schmerzen)"),
            ph("Der Arzt gibt mir eine Tablette.", "Bác sĩ cho tôi một viên thuốc. (Arzt + Tablette)"),
            ph("Nehmen Sie diese Tablette nach dem Essen!", "Uống viên thuốc này sau khi ăn! (Tablette)"),
            ph("Ohne Arzt keine Tablette.", "Không có bác sĩ thì không tự uống thuốc. (Arzt + Tablette)")
        ),
        "Sagen Sie: Was tun Sie, wenn Sie krank sind?",
        List.of(
            mcq("Wer hilft bei Krankheit?", "Arzt", "Arzt", "Bäcker", "Lehrer"),
            st("Wie heißt „viên thuốc“?", "Tablette"),
            mcq("„Ich habe Kopf...“ oft?", "Schmerzen", "Schmerzen", "Freude", "Urlaub")
        )));

    out.add(new LessonSpec(19, "Beruf & Arbeit — nghề nghiệp (A1)", LessonCategory.VOCABULARY,
        List.of(
            n("Lehrer", "der", "die Lehrer", "giáo viên (nam)", "[ˈleːʀɐ]", "Nữ: die Lehrerin; „Ich bin Lehrer.“"),
            n("Arbeit", "die", "die Arbeiten", "công việc", "[ˈaʁbaɪ̯t]", "„Ich habe viel Arbeit.“ — đi làm: zur Arbeit gehen."),
            w("lernen", "học", "[ˈlɛʁnən]", "„Ich lerne Deutsch.“ — khác studieren (đại học chuyên sâu hơn ở A1+).")),
        List.of(
            "Hỏi nghề: Was sind Sie von Beruf? / Was machen Sie beruflich?",
            "„Ich bin Lehrer / Lehrerin.“ — nghề + mạo từ nếu cần.",
            "„Zur Arbeit gehen“ = đi làm; lernen + Akkusativ: Deutsch lernen."
        ),
        List.of(
            ph("Mein Vater ist Lehrer.", "Bố tôi là giáo viên. (der Lehrer)"),
            ph("Ich habe heute viel Arbeit.", "Hôm nay tôi có nhiều việc. (die Arbeit)"),
            ph("Nach der Arbeit lerne ich Deutsch.", "Sau giờ làm tôi học tiếng Đức. (Arbeit + lernen)"),
            ph("Der Lehrer sagt: Lernt weiter!", "Giáo viên nói: Học tiếp đi! (Lehrer + lernen)"),
            ph("Arbeit und Lernen gehören zusammen.", "Làm việc và học tập đi cùng nhau. (Arbeit, lernen)")
        ),
        "Sagen Sie: Was sind Sie von Beruf? Oder: Was lernen Sie?",
        List.of(
            mcq("Wer unterrichtet oft in der Schule?", "Lehrer", "Lehrer", "Koch", "Pilot"),
            st("Wie sagt man „học“?", "lernen"),
            mcq("Was macht man im Büro oft?", "Arbeit", "Arbeit", "Urlaub", "Party")
        )));

    out.add(new LessonSpec(20, "Reisen — du lịch (A1)", LessonCategory.COMMUNICATION,
        List.of(
            n("Flughafen", "der", "die Flughäfen", "sân bay", "[ˈfluːkˌhaːfn̩]", "„Am Flughafen einchecken.“"),
            n("Ticket", "das", "die Tickets", "vé", "[ˈtɪkɪt]", "Vé máy bay/tàu; „ein Ticket buchen“."),
            n("Koffer", "der", "die Koffer", "vali", "[ˈkɔfɐ]", "„Ich packe den Koffer.“")),
        List.of(
            "„Am Flughafen“ — đến sân bay; Ticket buchen / ein Ticket haben.",
            "Hỏi điểm đến: Wohin fliegen Sie? — Nach + thành phố.",
            "„Koffer packen“ = sắp vali."
        ),
        List.of(
            ph("Wir treffen uns am Flughafen.", "Chúng tôi gặp nhau ở sân bay. (der Flughafen)"),
            ph("Mein Ticket liegt im Koffer.", "Vé của tôi nằm trong vali. (Ticket + Koffer)"),
            ph("Ohne Ticket kein Flug.", "Không có vé thì không bay. (Ticket)"),
            ph("Der Koffer ist schwer, das Ticket leicht.", "Vali nặng, vé nhẹ. (Koffer, Ticket)"),
            ph("Vom Flughafen zum Hotel — wo ist der Koffer?", "Từ sân bay đến khách sạn — vali ở đâu? (Flughafen, Koffer)")
        ),
        "Sagen Sie: Wohin reisen Sie gern?",
        List.of(
            mcq("Wo startet ein Flugzeug?", "Flughafen", "Flughafen", "Bahnhof", "Markt"),
            st("Wie heißt „vé“ (Neutral)?", "Ticket"),
            mcq("Wo packt man Kleidung für die Reise?", "Koffer", "Koffer", "Teller", "Tasche")
        )));

    out.add(new LessonSpec(21, "Feste & Geschenke — lễ và quà (A1)", LessonCategory.COMMUNICATION,
        List.of(
            n("Geburtstag", "der", "die Geburtstage", "sinh nhật", "[ɡəˈbʊʁtstak]", "„Alles Gute zum Geburtstag!“"),
            n("Geschenk", "das", "die Geschenke", "quà", "[ɡəˈʃɛŋk]", "„Ein Geschenk machen/bringen.“"),
            w("feiern", "ăn mừng / tổ chức", "[ˈfaɪ̯ɐn]", "„Wir feiern Geburtstag.“ — Party feiern.")),
        List.of(
            "Chúc mừng sinh nhật: Alles Gute zum Geburtstag!",
            "„Ein Geschenk machen/bringen“ — tặng quà.",
            "„feiern“ + Akk.: Geburtstag feiern, eine Party feiern."
        ),
        List.of(
            ph("Alles Gute zum Geburtstag!", "Chúc mừng sinh nhật! (der Geburtstag)"),
            ph("Ich habe ein Geschenk für dich.", "Tôi có quà cho bạn. (das Geschenk)"),
            ph("Wir feiern heute meinen Geburtstag.", "Hôm nay chúng tôi mừng sinh nhật tôi. (feiern + Geburtstag)"),
            ph("Zum Geburtstag gibt es viele Geschenke.", "Sinh nhật có nhiều quà. (Geburtstag + Geschenk)"),
            ph("Feiern wir zusammen!", "Cùng ăn mừng nhé! (feiern)")
        ),
        "Sagen Sie: Wann feiern Sie Geburtstag?",
        List.of(
            mcq("Was bekommt man oft am Geburtstag?", "Geschenk", "Geschenk", "Rechnung", "Strafe"),
            st("Wie heißt „sinh nhật“?", "Geburtstag"),
            mcq("Was macht man bei einer Party oft?", "feiern", "feiern", "schlafen", "reparieren")
        )));

    out.add(new LessonSpec(22, "Höflichkeit — lịch sự (A1)", LessonCategory.COMMUNICATION,
        List.of(
            w("bitte", "xin mời / làm ơn", "[ˈbɪtə]", "„Bitte schön.“ trả lời khi người khác cảm ơn; „Kaffee, bitte.“"),
            w("danke", "cảm ơn", "[ˈdaŋkə]", "„Danke schön.“ — „Nein, danke.“ lịch sự từ chối."),
            w("Entschuldigung", "xin lỗi", "[ɛntˈʃʊldɪɡʊŋ]", "Xin lỗi / xin nhắc lại: „Entschuldigung, wie bitte?“")),
        List.of(
            "„Bitte“: làm ơn / mời / không có chi (đáp lại cảm ơn tùy ngữ cảnh).",
            "„Danke (schön)“ — „Bitte (schön)“ thường đi cặp.",
            "„Entschuldigung“ mở đầu khi làm phiền hoặc xin nhắc lại."
        ),
        List.of(
            ph("Kaffee, bitte.", "Cà phê, làm ơn. (bitte)"),
            ph("Danke für die Hilfe!", "Cảm ơn vì đã giúp! (danke)"),
            ph("Bitte schön, gern geschehen.", "Không có chi, rất hân hạnh. (bitte)"),
            ph("Entschuldigung, wie bitte?", "Xin lỗi, bạn nói lại được không? (Entschuldigung + bitte)"),
            ph("Danke und bitte — ein schöner Dialog.", "Cảm ơn và làm ơn — một hội thoại hay. (danke, bitte)")
        ),
        "Üben Sie: Bitte, Danke, Entschuldigung in einem kurzen Dialog.",
        List.of(
            mcq("Was sagt man nach einem Geschenk oft?", "danke", "danke", "bitte", "tschüss"),
            st("Wie sagt man höflich „làm ơn“ oft (ein Wort)?", "bitte"),
            mcq("Wenn man stört, sagt man...", "Entschuldigung", "Entschuldigung", "Guten Appetit", "Prost")
        )));

    out.add(new LessonSpec(23, "Perfekt leicht — quá khứ cơ bản (A1)", LessonCategory.GRAMMAR,
        List.of(
            w("gehen", "đi", "[ˈɡeːən]", "„Ich gehe nach Hause.“ — Präsens; bất quy tắc: ging (Präteritum)."),
            w("gegangen", "đã đi (Partizip)", "[ɡəˈɡaŋən]", "Perfekt với sein: „Ich bin gegangen.“ (chuyển động)."),
            w("gestern", "hôm qua", "[ˈɡɛstɐn]", "Thời gian quá khứ: „Gestern war Sonntag.“")),
        List.of(
            "Perfekt gọn A1: sein/haben + Partizip II — „Ich bin … gegangen.“ (gehen).",
            "„Gestern“ đặt đầu câu hoặc sau chủ ngữ.",
            "Partizip II của gehen: gegangen (bất quy tắc)."
        ),
        List.of(
            ph("Gestern war Sonntag.", "Hôm qua là Chủ nhật. (gestern)"),
            ph("Ich gehe heute zu Fuß.", "Hôm nay tôi đi bộ. (gehen — hiện tại)"),
            ph("Gestern bin ich nach Hause gegangen.", "Hôm qua tôi đã về nhà. (gestern + gegangen)"),
            ph("Bist du schon gegangen?", "Bạn đã đi chưa? (gegangen)"),
            ph("Gehen wir zusammen?", "Chúng ta cùng đi nhé? (gehen)")
        ),
        "Sagen Sie kurz: Was haben Sie gestern gemacht?",
        List.of(
            mcq("Welches Wort bedeutet „hôm qua“?", "gestern", "gestern", "morgen", "heute"),
            st("Partizip II von „gehen“?", "gegangen"),
            mcq("Welches Verb passt zu Fuß?", "gehen", "gehen", "schwimmen", "fliegen")
        )));

    out.add(new LessonSpec(24, "Wiederholung A1 — ôn tập (A1)", LessonCategory.COMMUNICATION,
        List.of(
            w("Guten Morgen", "chào buổi sáng", "[ˈɡuːtn̩ ˈmɔʁɡn̩]", "Buổi sáng đến ~10–11h; tối: Guten Abend."),
            w("Tschüss", "tạm biệt (thân)", "[t͡ʃʏs]", "Thân mật; trang trọng hơn: Auf Wiedersehen."),
            w("Auf Wiedersehen", "hẹn gặp lại", "[aʊ̯f ˈviːdɐˌzeːən]", "Khi chia tay trang trọng; viết tắt: „Wiedersehen!“ ít gặp.")),
        List.of(
            "Ôn: chào hỏi theo thời điểm + tạm biệt (Tschüss / Auf Wiedersehen).",
            "Giới thiệu: Ich heiße … / Ich komme aus …",
            "Kết hợp lịch sự: Guten Tag + Entschuldigung + Danke."
        ),
        List.of(
            ph("Guten Morgen, Frau Meyer!", "Chào buổi sáng, bà Meyer! (Guten Morgen)"),
            ph("Guten Morgen und einen schönen Tag!", "Chào buổi sáng và chúc một ngày đẹp! (Guten Morgen)"),
            ph("Tschüss, bis gleich!", "Tạm biệt, gặp lại ngay! (Tschüss)"),
            ph("Auf Wiedersehen, Herr Doktor!", "Tạm biệt, bác sĩ! (Auf Wiedersehen)"),
            ph("Tschüss für heute, Auf Wiedersehen nächste Woche!", "Tạm biệt hôm nay, hẹn gặp lại tuần sau! (Tschüss + Auf Wiedersehen)")
        ),
        "Zusammenfassung: Begrüßen, verabschieden, sich vorstellen — 1 kurzer Monolog.",
        List.of(
            mcq("Was sagt man beim Verlassen formeller?", "Auf Wiedersehen", "Auf Wiedersehen", "Hallo", "Ja"),
            st("Wie sagt man „tạm biệt“ locker?", "Tschüss"),
            mcq("Am Morgen sagt man oft...", "Guten Morgen", "Guten Morgen", "Gute Nacht", "Guten Appetit")
        )));

    return List.copyOf(out);
  }
}
