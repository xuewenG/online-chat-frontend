<template>
  <el-row type="flex"
          justify="space-between"
          :class="[{'activeChat': active}, {'unactiveChat': !active}, 'groupItem']"
          @click.native="changeChat">
      <el-col :span="8">
          <el-avatar size="medium" shape="square" :src="group.avatar" class="avatar" @click.native="showProfile"></el-avatar>
      </el-col>
      <el-col :span="16" class="groupnameItem">
          <el-row class="title">
            <el-col class="name">{{groupName}}</el-col>
          </el-row>
          <el-row class="detail">
            <el-col :span="18" class="message">{{ calculateLastMessage.message }}</el-col>
            <el-col :span="6" class="time">{{ calculateLastMessage.time }}</el-col>
          </el-row>
      </el-col>
      <el-badge v-if="!!newMessageNumber && newMessageNumber > 0" :value="newMessageNumber">
      </el-badge>
  </el-row>
</template>

<script>
    const ipc = require('electron').ipcRenderer
    export default {
      name: 'GroupItem',
      props: ['group', 'active'],
      data () {
        return {
          newMessageNumber: 0
        }
      },
      computed: {
        currentGroupChat () {
          return this.$store.getters.currentGroupChat
        },
        groupList () {
          return this.$store.getters.groupList
        },
        groupData () {
          for (const group of this.groupList) {
            if (group.id === this.group.id) {
              return group
            }
          }
        },
        groupName () {
          return this.limitedString(this.group.name, 9)
        },
        groupMessageList () {
          return this.$store.getters.groupMessageList
        },
        currentMessageList () {
          for (const groupMessage of this.groupMessageList) {
            if (groupMessage.groupId === this.group.id) {
              return groupMessage.messageList
            }
          }
        },
        calculateLastMessage () {
          let result = {
            message: '',
            time: ''
          }
          let currentLastMessage = (this.currentMessageList && this.currentMessageList.length > 0) ? this.currentMessageList[this.currentMessageList.length - 1] : null
          if (currentLastMessage) {
            result.message = currentLastMessage.content
            let last = new Date(currentLastMessage.time)
            let year = last.getFullYear()
            let month = last.getMonth()
            let day = last.getDate()
            let now = new Date()
            if (year === now.getFullYear()) {
              if (month === now.getMonth() && day === now.getDate()) {
                let hour = last.getHours()
                if (hour < 10) hour = '0' + hour
                let minute = last.getMinutes()
                if (minute < 10) minute = '0' + minute
                result.time = hour + ':' + minute
              } else {
                result.time = (month + 1) + '-' + day
              }
            } else {
              result.time = year + '-' + (month + 1) + '-' + day
            }
            // console.log(this.result)
          }
          result.message = this.limitedString(result.message, 7)
          // console.log(result)
          return result
        }
      },
      methods: {
        getGroupMemberList () {
          this.axios.post('/group/getGroupUserList', {groupId: this.currentGroupChat}).then(res => {
            console.log(res)
            if (res.data.code === '0000') {
              this.$store.dispatch('addGroupMemberListAction', res.data.data.groupUserList)
            }
          })
        },
        changeChat () {
          if (this.currentGroupChat !== this.group.id) {
            this.$store.dispatch('changeCurrentGroupChatAction', this.group.id)
            this.getGroupMemberList()
          }
        },
        showProfile () {
          ipc.send('groupInfo')
          ipc.send('getGroup', this.group)
        },
        limitedString (string, limit) {
          let str = ''
          if (string.length > limit && limit >= 0) {
            str = string.slice(0, limit) + '...'
            return str
          }
          return string
        }
      },
      created () {
      },
      watch: {
        groupData: {
          deep: true,
          handler (nv, ov) {
            this.newMessageNumber = nv.newMessageNumber
          }
        }
      }

    }
</script>

<style scoped>
  .groupItem {
    height: 80px;
  }

  .unactiveChat:hover {
    background-color: #f0f0f0;
  }

  .activeChat {
    background-color: #e0e0e0;
  }

  .avatar {
    margin: 7px 15px;
    height: 66px;
    width: 65px;
  }

  /* .groupnameItem {
    margin: 7px 0;
  }

  .groupname {
    color: black;
    font-weight: 700;
    font-size: 20px;
    margin-top: 5px;
  }

  .account {
    color: gray;
    margin-top: 10px;
  } */

  .nicknameItem {
    margin: 7px 0;
  }

  .title {
    margin-top: 5px;
    overflow: hidden;
  }

  .name {
    color: black;
    font-weight: 700;
    font-size: 20px;
  }

  .detail {
    font-size: 90%;
    color: gray;
    margin-top: 10px;
  }
  
  .message {
    overflow: hidden;
  }
  
  .time {
    text-align: right;
    /* font-size: 80%; */
    color: gray;
  }
</style>
