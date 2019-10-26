<template>
  <q-page class="flex flex-center">
  </q-page>
</template>

<script>
export default {
  name: "PageIndex",
  data: function() {
    return {
      messageinput: "&#128513;",
      editor: ClassicEditor,
      editorData: '<p>Content of the editor.</p>',
      editorConfig: {
        // The configuration of the editor.
      }
    };
  },
  methods: {
    add: function(text) {
      this.messageinput += text;
    }
  },
  computed: {
    emojis: () => {
      let emojiList = [];
      const emojRange = [128513, 128581];
      //inside emojRange 2d array , define range arrays (start number,end number).
      //1st array : Emoticons icons
      //2nd range : Dingbats.
      //3rd range : Transport and map symbols
      let range = emojRange;
      for (let x = range[0]; x < range[1]; x++) {
        emojiList.push("&#" + x + ";");
      }
      return emojiList;
    }
  }
};
</script>

<style scoped>
.denseemoji {
  margin: 0px;
}
</style>
