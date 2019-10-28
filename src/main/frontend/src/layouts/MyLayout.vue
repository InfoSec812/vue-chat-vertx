<template>
  <div>
    <div class="header">
      STP Chat
    </div>
    <div class="bodyContent" ref="scrollArea">
      <q-chat-message
        v-for="(msg, index) in sortedMessages"
        :key="index"
        :text="[msg.msg]"
        :name="msg.username"
        :stamp="msg.timestamp | dateTime"
        :avatar="msg.avatar"
        :sent="msg.sent"
      />
    </div>
    <div class="footer row">
      <q-editor
        v-model="messageinput"
        ref="editorInput"
        :dense="$q.screen.lt.md"
        @change="sendMessage"
        style="width: 90vw;"
      />
      <q-btn icon="send" @click="sendMessage" dense style="width: 30px;" />
    </div>
  </div>
</template>

<script>
export default {
  name: "MyLayout",

  data: function() {
    return {
      messageinput: "",
      messageList: [],
      userhash: ""
    };
  },
  computed: {
    sortedMessages: function() {
      return this.messageList.slice(0).sort(function(a, b) {
        return new Date(a.timestamp) - new Date(b.timestamp);
      });
    }
  },
  filters: {
    dateTime: value => {
      let dateObject = new Date(value);
      let currentTime = new Date();
      if (dateObject.getDate() === currentTime.getDate()) {
        if (dateObject.getHours() === currentTime.getHours()) {
          if (dateObject.getMinutes() === currentTime.getMinutes()) {
            return "a few seconds";
          } else {
            let minDiff = currentTime.getMinutes() - dateObject.getMinutes();
            return "~" + minDiff + " minutes ago";
          }
        } else {
          let hoursDiff = currentTime.getHours() - dateObject.getHours();
          return "~" + hoursDiff + " hours ago";
        }
      } else {
        return dateObject.toLocaleString();
      }
    }
  },
  methods: {
    sendMessage: function() {
      const holder = this.messageinput;
      if (this.messageinput.length > 0) {
        this.messageinput = "";
        let timestamp = new Date().toISOString();
        this.$eventBus.publish("messages", {
          msg: holder,
          timestamp: timestamp,
          username: this.username,
          avatar: this.userhash,
          sent: false
        });
      }
      this.$refs.editorInput.focus();
    },
    gravatar: () => {
      let length = 32;
      let result = "";
      let characters = "1234567890abcdef";
      let charactersLength = characters.length;
      for (let i = 0; i < length; i++) {
        result += characters.charAt(
          Math.floor(Math.random() * charactersLength)
        );
      }
      let avatarUrl = "https://www.gravatar.com/avatar/" + result + "?d=robohash";
      return avatarUrl.trim();
    }
  },
  watch: {
    messageList: {
      deep: true,
      handler() {
        this.$nextTick(() => {
          this.$refs.scrollArea.scrollTop = this.$refs.scrollArea.scrollHeight;
        });
      }
    }
  },
  eventbus: {
    lifecycleHooks: {
      mounted(context, eventbus) {
        eventbus.registerHandler("messages", (error, res) => {
          if (error === null) {
            res.body["sent"] = true;
            if (res.body.avatar === context._data.userhash) {
              res.body["sent"] = false;
            }
            context._data.messageList.push(res.body);
            if (context._data.messageList.length > 200) {
              context._data.messageList.shift();
            }
          } else {
            console.log(
              "Error connecting to event bus bridge: " + JSON.stringify(error)
            );
          }
        });
      },
    }
  },
  mounted() {
    // When this layout is first created, choose a random gravatar avatar
    this.userhash = this.gravatar();
  }
};
</script>

<style>
body {
  overflow: hidden;
}
.header {
  position: fixed;
  padding-left: 10px;
  padding-top: 6px;
  z-index: 2;
  top: 0px;
  height: 60px;
  width: 100%;
  background-color: rgb(238, 0, 0);
  box-shadow: 5px 5px 5px 5px rgba(0, 0, 0, 0.2);
  font-family: Overpass sans-serif;
  font-size: 26pt;
  font-weight: bold;
  color: white;
}
.footer {
  position: absolute;
  bottom: 0px;
  width: 100%;
  height: 100px;
  box-shadow: -5px -5px 5px -5px rgba(0, 0, 0, 0.2);
}
.bodyContent {
  position: absolute;
  top: 60px;
  bottom: 110px;
  overflow: auto;
  width: 100vw;
}
</style>
