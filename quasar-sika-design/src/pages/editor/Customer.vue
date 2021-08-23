<template>
  <div>
    <tinymce ref="editor" :key="tinymceFlag" v-model="goodsDescp" />
  </div>
</template>
<script>
import tinymce from '@tinymce/tinymce-vue'
export default {
  components: { tinymce },
  data() {
    return {
      tinymceFlag: 'editor',
      goodsDescp: ''
    }
  }
}
</script>
