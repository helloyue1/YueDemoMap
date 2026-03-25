import { describe, it, expect, vi, beforeEach } from 'vitest'
import {
  validateImageFile,
  formatFileSize,
  getFileExtension,
  isImageFile,
  extractRelativePath,
  buildImageUrl,
  parseImageList,
  ALLOWED_TYPES,
  MAX_SIZE_MB,
  MAX_SIZE_BYTES
} from '../imageUpload'

describe('图片上传工具函数测试', () => {
  describe('validateImageFile', () => {
    it('应该验证有效的图片文件', () => {
      const validFile = new File(['test'], 'test.jpg', { type: 'image/jpeg' })
      Object.defineProperty(validFile, 'size', { value: 1024 * 1024 }) // 1MB
      
      const result = validateImageFile(validFile)
      expect(result.valid).toBe(true)
      expect(result.errors).toHaveLength(0)
    })

    it('应该拒绝无效的文件类型', () => {
      const invalidFile = new File(['test'], 'test.pdf', { type: 'application/pdf' })
      Object.defineProperty(invalidFile, 'size', { value: 1024 })
      
      const result = validateImageFile(invalidFile)
      expect(result.valid).toBe(false)
      expect(result.errors).toContain(expect.stringContaining('不支持的图片格式'))
    })

    it('应该拒绝过大的文件', () => {
      const largeFile = new File(['test'], 'test.jpg', { type: 'image/jpeg' })
      Object.defineProperty(largeFile, 'size', { value: 10 * 1024 * 1024 }) // 10MB
      
      const result = validateImageFile(largeFile)
      expect(result.valid).toBe(false)
      expect(result.errors).toContain(expect.stringContaining('超过'))
    })

    it('应该拒绝空文件', () => {
      const result = validateImageFile(null)
      expect(result.valid).toBe(false)
      expect(result.errors).toContain('请选择文件')
    })
  })

  describe('formatFileSize', () => {
    it('应该正确格式化字节', () => {
      expect(formatFileSize(0)).toBe('0 B')
      expect(formatFileSize(1024)).toBe('1 KB')
      expect(formatFileSize(1024 * 1024)).toBe('1 MB')
      expect(formatFileSize(1024 * 1024 * 1024)).toBe('1 GB')
    })

    it('应该保留两位小数', () => {
      expect(formatFileSize(1536)).toBe('1.5 KB')
    })
  })

  describe('getFileExtension', () => {
    it('应该正确获取文件扩展名', () => {
      expect(getFileExtension('test.jpg')).toBe('.jpg')
      expect(getFileExtension('test.PNG')).toBe('.png')
      expect(getFileExtension('test.file.name.jpg')).toBe('.jpg')
    })

    it('应该为无扩展名文件返回默认扩展名', () => {
      expect(getFileExtension('test')).toBe('.jpg')
      expect(getFileExtension(null)).toBe('.jpg')
    })
  })

  describe('isImageFile', () => {
    it('应该识别图片文件', () => {
      const imageFile = new File(['test'], 'test.jpg', { type: 'image/jpeg' })
      expect(isImageFile(imageFile)).toBe(true)
    })

    it('应该拒绝非图片文件', () => {
      const pdfFile = new File(['test'], 'test.pdf', { type: 'application/pdf' })
      expect(isImageFile(pdfFile)).toBe(false)
    })
  })

  describe('extractRelativePath', () => {
    it('应该从完整URL中提取相对路径', () => {
      const url = 'http://localhost:3001/uploads/rooms/20260208/test.jpg'
      expect(extractRelativePath(url)).toBe('rooms/20260208/test.jpg')
    })

    it('应该从/upload路径中提取相对路径', () => {
      const url = '/uploads/rooms/20260208/test.jpg'
      expect(extractRelativePath(url)).toBe('rooms/20260208/test.jpg')
    })

    it('应该返回已经是相对路径的字符串', () => {
      const path = 'rooms/20260208/test.jpg'
      expect(extractRelativePath(path)).toBe(path)
    })

    it('应该处理blob URL', () => {
      expect(extractRelativePath('blob:http://localhost/abc123')).toBe('')
    })

    it('应该处理空值', () => {
      expect(extractRelativePath('')).toBe('')
      expect(extractRelativePath(null)).toBe('')
    })
  })

  describe('buildImageUrl', () => {
    it('应该构建完整URL', () => {
      const relativePath = 'rooms/20260208/test.jpg'
      expect(buildImageUrl(relativePath, 'http://localhost:3001'))
        .toBe('http://localhost:3001/uploads/rooms/20260208/test.jpg')
    })

    it('应该返回已经是完整URL的字符串', () => {
      const fullUrl = 'http://example.com/image.jpg'
      expect(buildImageUrl(fullUrl)).toBe(fullUrl)
    })

    it('应该处理以/uploads/开头的路径', () => {
      const path = '/uploads/rooms/test.jpg'
      expect(buildImageUrl(path, 'http://localhost:3001'))
        .toBe('http://localhost:3001/uploads/rooms/test.jpg')
    })

    it('应该处理空值', () => {
      expect(buildImageUrl('')).toBe('')
      expect(buildImageUrl(null)).toBe('')
    })
  })

  describe('parseImageList', () => {
    it('应该解析逗号分隔的图片列表', () => {
      const images = 'rooms/20260208/test1.jpg, rooms/20260208/test2.jpg'
      const result = parseImageList(images)
      
      expect(result).toHaveLength(2)
      expect(result[0].relativePath).toBe('rooms/20260208/test1.jpg')
      expect(result[1].relativePath).toBe('rooms/20260208/test2.jpg')
    })

    it('应该过滤空值', () => {
      const images = 'rooms/test1.jpg,, rooms/test2.jpg, '
      const result = parseImageList(images)
      
      expect(result).toHaveLength(2)
    })

    it('应该处理空字符串', () => {
      expect(parseImageList('')).toHaveLength(0)
      expect(parseImageList(null)).toHaveLength(0)
    })
  })

  describe('常量', () => {
    it('应该有正确的允许类型', () => {
      expect(ALLOWED_TYPES).toContain('image/jpeg')
      expect(ALLOWED_TYPES).toContain('image/png')
      expect(ALLOWED_TYPES).toContain('image/gif')
      expect(ALLOWED_TYPES).toContain('image/webp')
    })

    it('应该有正确的大小限制', () => {
      expect(MAX_SIZE_MB).toBe(5)
      expect(MAX_SIZE_BYTES).toBe(5 * 1024 * 1024)
    })
  })
})
