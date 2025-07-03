import React, { useState, useEffect } from 'react';
import './index.css';

interface GuideProps {
  onComplete: () => void;
}

const Guide: React.FC<GuideProps> = ({ onComplete }) => {
  const [currentSlide, setCurrentSlide] = useState(0);
  const totalSlides = 4;

  const slides = [
    {
      icon: '🗺️',
      title: '知识地图',
      descriptions: [
        '以地图形式展示知识，让你从宏观上把握知识体系，就像探索一块新大陆。',
        '不同区域代表不同知识领域，路径连接相关知识点。'
      ],
      color: '#c0a080'
    },
    {
      icon: '🌳',
      title: '知识树',
      descriptions: [
        '知识如树木般生长，从根基知识到枝叶延伸，一目了然。',
        '随着学习的深入，见证你的知识树逐渐枝繁叶茂。'
      ],
      color: '#4caf50'
    },
    {
      icon: '🔮',
      title: '知识球',
      descriptions: [
        '立体展现知识之间的复杂关联，多维度理解知识。',
        '旋转、缩放，从不同角度探索知识之间的联系。'
      ],
      color: '#9c27b0'
    },
    {
      icon: '🚀',
      title: '智慧成长',
      descriptions: [
        '科学的复习提醒，让记忆更加牢固。',
        '趣味的学习方式，让你在游戏中巩固知识。'
      ],
      color: '#1e88e5'
    }
  ];

  const goToSlide = (index: number) => {
    if (index < 0) {
      index = 0;
    } else if (index >= totalSlides) {
      onComplete();
      return;
    }
    setCurrentSlide(index);
  };

  const handleNext = () => {
    goToSlide(currentSlide + 1);
  };

  const handleSkip = () => {
    onComplete();
  };

  // 支持键盘导航
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'ArrowRight' || e.key === ' ') {
        e.preventDefault();
        handleNext();
      } else if (e.key === 'ArrowLeft') {
        e.preventDefault();
        goToSlide(currentSlide - 1);
      } else if (e.key === 'Escape') {
        e.preventDefault();
        handleSkip();
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [currentSlide]);

  // 支持触摸手势
  const [touchStart, setTouchStart] = useState(0);
  const [touchEnd, setTouchEnd] = useState(0);

  const handleTouchStart = (e: React.TouchEvent) => {
    setTouchStart(e.targetTouches[0].clientX);
  };

  const handleTouchMove = (e: React.TouchEvent) => {
    setTouchEnd(e.targetTouches[0].clientX);
  };

  const handleTouchEnd = () => {
    if (!touchStart || !touchEnd) return;
    
    const distance = touchStart - touchEnd;
    const isLeftSwipe = distance > 50;
    const isRightSwipe = distance < -50;

    if (isLeftSwipe) {
      handleNext();
    } else if (isRightSwipe) {
      goToSlide(currentSlide - 1);
    }
  };

  return (
    <div className="guide-container">
      <div 
        className="slide-wrapper"
        onTouchStart={handleTouchStart}
        onTouchMove={handleTouchMove}
        onTouchEnd={handleTouchEnd}
      >
        <div 
          className="slides-container"
          style={{ transform: `translateX(-${currentSlide * 100}%)` }}
        >
          {slides.map((slide, index) => (
            <div key={index} className="slide">
              <div className="slide-content">
                <div 
                  className="slide-image"
                  style={{ backgroundColor: slide.color }}
                >
                  <span className="slide-icon">{slide.icon}</span>
                </div>
                <h2 className="slide-title">{slide.title}</h2>
                {slide.descriptions.map((desc, descIndex) => (
                  <p key={descIndex} className="slide-description">
                    {desc}
                  </p>
                ))}
                {index === totalSlides - 1 && (
                  <div className="start-button-container">
                    <button className="start-button" onClick={onComplete}>
                      开始体验
                    </button>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>

      <div className="guide-footer">
        <button className="skip-button" onClick={handleSkip}>
          跳过
        </button>
        
        <div className="progress-dots">
          {Array.from({ length: totalSlides }, (_, index) => (
            <div
              key={index}
              className={`dot ${index === currentSlide ? 'active' : ''}`}
              onClick={() => goToSlide(index)}
            />
          ))}
        </div>
        
        <button className="next-button" onClick={handleNext}>
          {currentSlide === totalSlides - 1 ? '开始体验' : '下一步'}
        </button>
      </div>
    </div>
  );
};

export default Guide;
